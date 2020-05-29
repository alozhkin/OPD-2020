package scraper;

import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import spider.FailedPage;
import spider.HtmlLanguageException;
import spider.Page;
import splash.*;
import utils.Html;
import utils.Link;
import utils.WrongFormedLinkException;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static logger.LoggerUtils.*;

/**
 * Class that uses <a href="https://splash.readthedocs.io">Splash</a> - lightweight web browser with an HTTP API
 * to scrape pages.
 * <p>
 * Splash is restarting when eats up too much RAM.
 * In that period splash may return 503 or 502 and some connections may fail with {@link EOFException}
 * or {@link SocketException}.
 * So program schedules request retry specified number of times with some delay.
 * If splash will not answer, url will be added to failed pages with {@link SplashNotRespondingException}
 * If splash redirects to page without valid url, will not scrape it
 * If page contains iframes, ignores frames that leads to another sites and scrape frames that leads to another pages
 */
public class SplashScraper implements Scraper {
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();
    private static final ScheduledExecutorService retryExecutor = Executors.newSingleThreadScheduledExecutor();
    // in millis
    private static final int SPLASH_RESTART_TIME = 3000;
    private static final int SPLASH_RETRY_TIMEOUT = 500;
    private static final int SPLASH_IS_UNAVAILABLE_RETRIES = 10;
    private static final Gson gson = new Gson();

    private final Statistic stat = new Statistic();
    private final SplashRequestFactory renderReqFactory;
    // contains calls that are proceeded by http client or retry schedule executor
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final ConcurrentLinkedQueue<FailedPage> failedPages = new ConcurrentLinkedQueue<>();
    private final AtomicInteger scheduledToRetry = new AtomicInteger(0);
    private final AtomicReference<String> domain = new AtomicReference<>();

    public SplashScraper(SplashRequestFactory renderReqFactory) {
        this.renderReqFactory = renderReqFactory;
    }

    /**
     * Shuts down http client and executor service, use only once
     */
    public static void shutdown() {
        try {
            retryExecutor.shutdown();
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
            var cache = httpClient.cache();
            if (cache != null) {
                cache.close();
            }
        } catch (IOException e) {
            debugLog.error("SplashScraper - Cache closing rejected", e);
        }
    }

    @Override
    public void scrape(Link link, Consumer<Page> siteConsumer) {
        var request = renderReqFactory.getRequest(new DefaultSplashRequestContext.Builder().setSiteUrl(link).build());
        var call = httpClient.newCall(request);
        calls.add(call);
        call.enqueue(new SplashCallback(new CallContext(link, siteConsumer)));
        stat.requestSended();
    }

    /**
     * Returns number of pages which are being processed.
     * <p>
     * Takes into account requests that are proceeded by http client
     * and requests that only waiting to be retried.
     *
     * @return number of pages which are being processed
     */
    @Override
    public int scrapingPagesCount() {
        // order is important
        return scheduledToRetry.get() + httpClient.dispatcher().runningCallsCount();
    }

    /**
     * Cancels requests that are proceeded by http client and requests that only waiting to be retried
     */
    @Override
    public void cancelAll() {
        synchronized (calls) {
            calls.forEach(Call::cancel);
        }
    }

    @Override
    public List<FailedPage> getFailedPages() {
        return new ArrayList<>(failedPages);
    }

    /**
     * Returns object with information about requests and responses
     *
     * @return statistic
     */
    public Statistic getStatistic() {
        return stat;
    }


    /**
     * Class that contains all logic in charge of handling response
     */
    private class SplashCallback implements Callback {
        private final Link initialLink;
        private final Consumer<Page> consumer;
        private final CallContext context;
        private Link finalLink;
        private Call call;

        public SplashCallback(CallContext context) {
            this.initialLink = context.getLink();
            this.consumer = context.getConsumer();
            this.context = context;
        }

        /**
         * Logs failures, saves failed pages, considers {@link EOFException} and {@link SocketException}
         * like signs of Splash restarting and retries
         *
         * @param call call to http client
         * @param e exception
         */
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            this.call = call;
            if (!call.isCanceled()) {
                handleFail(e);
            }
            calls.remove(call);
        }

        private void handleFail(IOException e) {
            var cause = e.getCause();
            if (cause != null && cause.getClass().equals(EOFException.class)) {
                handleSplashRestarting(cause.getClass().getSimpleName());
                return;
            } else if (e.getClass().equals(SocketException.class)) {
                handleSplashRestarting(e.getClass().getSimpleName());
                return;
            } else if (e.getMessage().equals("Canceled")) {
                debugLog.debug("SplashScraper - Request canceled " + initialLink);
            } else if (e.getMessage().equals("executor rejected")) {
                debugLog.error("SplashScraper - Executor rejected {}", initialLink);
                stat.requestFailed();
            } else {
                debugLog.error("SplashScraper - Request failed {}", initialLink, e);
                stat.requestFailed();
            }
            failedPages.add(new FailedPage(e, initialLink));
        }

        private void handleSplashRestarting(String reason) {
            var delay = getDelay(context.getRetryCount());
            if (delay == -1) {
                throw new SplashNotRespondingException();
            } else {
                scheduleToRetry(delay, reason);
            }
        }

        /**
         * Gives delay for next retry.
         * <p>
         * Returns -1 if retry count is done
         *
         * @param retryCount number of retries
         * @return delay (in millis)
         */
        private int getDelay(int retryCount) {
            var delay = -1;
            if (retryCount == 0) {
                delay = SPLASH_RESTART_TIME;
            } else if (retryCount < SPLASH_IS_UNAVAILABLE_RETRIES) {
                delay = SPLASH_RETRY_TIMEOUT;
            }
            return delay;
        }

        private void scheduleToRetry(int delay, String reason) {
            logRetry(reason);
            synchronized (calls) {
                if (!call.isCanceled()) {
                    var newCall = call.clone();
                    calls.add(newCall);
                    retryExecutor.schedule(() -> retry(newCall), delay, TimeUnit.MILLISECONDS);
                }
            }
        }

        private void logRetry(String reason) {
            scheduledToRetry.incrementAndGet();
            debugLog.warn("SplashScraper - {}, request will be retried {}", reason, initialLink);
        }

        private void retry(Call call) {
            if (!call.isCanceled()) {
                call.enqueue(new SplashCallback(context.getForNewRetry()));
                scheduledToRetry.decrementAndGet();
                stat.requestRetried();
            }
        }

        /**
         * Extracts html and final link from response and and gives them to consumer.
         * <p>
         * Considers HTTP 502 and HTTP 503 like signs of Splash restarting and retries.
         * HTTP 200 is the only code which allows html consuming.
         * If exception is thrown, logs it and saves in failed pages.
         *
         * @param call call to http client
         * @param response response from splash
         */
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            debugLog.debug("SplashScraper - Response is accepted {}", initialLink);
            this.call = call;
            try {
                if (!call.isCanceled()) {
                    handleResponse(response);
                }
            } catch (Exception e) {
                handleExceptionOnResponse(e);
            }
            calls.remove(call);
        }

        private void handleResponse(Response response) throws IOException {
            int code = response.code();
            if (code == 200) {
                handleSuccessfulResponse(response);
            } else if (code == 503 || code == 502) {
                handleServerProblem(response, code);
            } else if (code == 504) {
                handleTimeoutResponse();
            }  else if (code == 400) {
                handle400Response(response);
            } else {
                handleUnexpectedResponse(code);
            }
            response.close();
        }

        public void handleSuccessfulResponse(Response response) throws IOException {
            stat.requestSucceeded();
            var body = extractResponseBode(response);
            var splashResponse = gson.fromJson(body, SplashResponse.class);
            var splashUrl = splashResponse.getUrl();
            if (splashUrl.equals("")) {
                var info = new Response400Info();
                if (splashResponse.getHtml().contains("Network error #301")) {
                    info.setError("network301");
                } else {
                    info.setError("unknown error, url is missing");
                }
                throw new SplashScriptExecutionException(info);
            }
            finalLink = new Link(splashResponse.getUrl());
            if (!isSameSite()) return;
            logRedirect();
            if (!call.isCanceled()) {
                var frames = getFrames(splashResponse);
                consumer.accept(new Page(new Html(splashResponse.getHtml(), finalLink), initialLink, frames));
                stat.siteScraped();
            }
        }

        private String extractResponseBode(Response response) throws IOException {
            var responseBody = response.body();
            if (responseBody == null) {
                throw new ScraperConnectionException("Response body is absent");
            }
            return responseBody.string();
        }

        private ArrayList<Html> getFrames(SplashResponse splashResponse) {
            var frames = new ArrayList<Html>();
            var frameCollection = splashResponse.getFrames();
            if (frameCollection != null) {
                frames.addAll(Arrays.stream(frameCollection)
                        .map(strFrame -> new Html(strFrame, finalLink))
                        .collect(Collectors.toList()));
            }
            return frames;
        }

        /**
         * Makes sure that we stayed on same site
         *
         * @return {@code true} if it first scraped site, or redirect last point is on same domain or subdomain
         * {@code false} if redirected not on same domain or subdomain (on another site)
         */
        private boolean isSameSite() {
            if (isDomainSuitable()) {
                return true;
            } else {
                debugLog.info("SplashScraper - Tried to redirect from {} to site {}", initialLink, finalLink);
                stat.responseRejected();
                return false;
            }
        }

        private boolean isDomainSuitable() {
            var scrapedUrlHostWithoutWWW = finalLink.fixWWW().getHost();
            if (domain.get() == null) {
                domain.set(scrapedUrlHostWithoutWWW);
            } else {
                return scrapedUrlHostWithoutWWW.contains(domain.get());
            }
            return true;
        }

        private void logRedirect() {
            var isRedirected = !finalLink.getWithoutProtocol().equals(initialLink.getWithoutProtocol());
            if (isRedirected) {
                debugLog.info("SplashScraper - Redirect from {} to {}", initialLink, finalLink);
            }
        }

        private void handleServerProblem(Response response, int code) {
            if (!Objects.equals(response.header("Retry-After"), "-1")) {
                handleSplashRestarting("Splash is unavailable");
            } else {
                debugLog.info("SplashScraper - HTTP {}, page {} was not scraped", code, initialLink);
            }
        }

        private void handleTimeoutResponse() {
            debugLog.warn("SplashScraper - Timeout expired, page {} was not scraped", initialLink);
            stat.requestTimeout();
        }

        private void handle400Response(Response response) throws IOException {
            var body = extractResponseBode(response);
            var splashResponse = gson.fromJson(body, Splash400Response.class);
            var type = splashResponse.getType();
            if (type != null && type.equals("ScriptError")) {
                throw new SplashScriptExecutionException(splashResponse.getInfo());
            }
            debugLog.error("SplashScraper - Unexpected 400 HTTP {}", body);
            stat.responseFailCode();
        }

        private void handleUnexpectedResponse(int code) {
            debugLog.info("SplashScraper - HTTP {}, page {} was not scraped", code, initialLink);
            stat.responseFailCode();
        }

        private void handleExceptionOnResponse(Exception e) {
            failedPages.add(new FailedPage(e, initialLink));
            var exClass = e.getClass();
            if (exClass.equals(HtmlLanguageException.class)) {
                debugLog.warn("SplashScraper - Wrong html language {}", initialLink.toString());
                stat.responseRejected();
                return;
            } else if (exClass.equals(SplashScriptExecutionException.class)) {
                handleSplashScriptException((SplashScriptExecutionException) e);
            } else if (exClass.equals(WrongFormedLinkException.class)) {
                debugLog.error("SplashScraper - {}", e.getMessage(), e);
            } else if (exClass.equals(ScraperConnectionException.class)) {
                debugLog.error("Spider - Request failed {} {}", domain, e.getClass().getSimpleName());
            } else if (!exClass.equals(SplashNotRespondingException.class) && !exClass.equals(SocketException.class)) {
                debugLog.error("SplashScraper - Exception on response, site {}", initialLink, e);
            }
            stat.responseException();
        }

        private void handleSplashScriptException(SplashScriptExecutionException splashEx) {
            var error = splashEx.getInfo().getError();
            if (error.startsWith("network")) {
                if (error.equals("network3")) {
                    debugLog.warn("SplashScraper - No address associated with host name {}",
                            initialLink.toString());
                } else {
                    debugLog.warn("SplashScraper - Splash execution network exception {} {}",
                            initialLink.toString(), splashEx.getInfo());
                }
            } else {
                if (error.equals("webkit102")) {
                    debugLog.warn("SplashScraper - Splash does not support pdf {} {}",
                            initialLink.toString(), splashEx.getInfo());
                } else {
                    debugLog.error("SplashScraper - Splash execution exception {} {}",
                            initialLink.toString(), splashEx.getInfo());
                }
            }
        }
    }

    /**
     * Little data class with information useful for calls
     */
    private static class CallContext {
        private final Link link;
        private final Consumer<Page> consumer;
        private final int retryCount;

        public CallContext(Link link, Consumer<Page> consumer) {
            this(link, consumer, 0);
        }

        public CallContext(Link link, Consumer<Page> consumer, int retryCount) {
            this.link = link;
            this.consumer = consumer;
            this.retryCount = retryCount;
        }

        public Link getLink() {
            return link;
        }

        public Consumer<Page> getConsumer() {
            return consumer;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public CallContext getForNewRetry() {
            return new CallContext(link, consumer, retryCount + 1);
        }
    }
}
