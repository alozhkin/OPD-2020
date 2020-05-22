package scraper;

import com.google.gson.Gson;
import logger.LoggerUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

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
    private final List<FailedPage> failedPages = new ArrayList<>();
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
            LoggerUtils.debugLog.error("SplashScraper - Cache closing rejected", e);
        }
    }

    @Override
    public void scrape(Link link, Consumer<Page> siteConsumer) {
        LoggerUtils.debugLog.debug("SplashScraper - Site start {}", link);
        var request = renderReqFactory.getRequest(new DefaultSplashRequestContext.Builder().setSiteUrl(link).build());
        var call = httpClient.newCall(request);
        calls.add(call);
        call.enqueue(new SplashCallback(new CallContext(link, siteConsumer)));
        stat.requestSended();
        LoggerUtils.debugLog.debug("SplashScraper - Site end {}", link);
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
        return failedPages;
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
            handleFail(e);
            calls.remove(call);
        }

        private void handleFail(IOException e) {
            var cause = e.getCause();
            if (cause != null && cause.getClass().equals(EOFException.class)) {
                LoggerUtils.debugLog.warn("SplashScraper - EOFException, request will be retried {}", initialLink);
                handleSplashRestarting();
                return;
            } else if (e.getMessage().equals("Canceled")) {
                LoggerUtils.debugLog.warn("SplashScraper - Request canceled " + initialLink);
            } else if (e.getClass().equals(SocketException.class)) {
                LoggerUtils.debugLog.error("SplashScraper - Socket is closed, request will be retried {}", initialLink);
                handleSplashRestarting();
                return;
            } else if (e.getMessage().equals("executor rejected")) {
                LoggerUtils.debugLog.error("SplashScraper - Executor rejected {}", initialLink);
                stat.requestFailed();
            } else {
                LoggerUtils.debugLog.error("SplashScraper - Request failed {}", initialLink, e);
                stat.requestFailed();
            }
            failedPages.add(new FailedPage(e, initialLink));
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
            LoggerUtils.debugLog.debug("SplashScraper - Response is accepted {}", initialLink);
            this.call = call;
            try {
                handleResponse(response);
            } catch (Exception e) {
                handleExceptionOnResponse(e);
            }
            calls.remove(call);
        }

        private void handleResponse(Response response) throws IOException {
            int code = response.code();
            if (code == 503 || code == 502) {
                LoggerUtils.debugLog.warn("SplashScraper - HTTP {}, request will be retried {}", code, initialLink);
                handleSplashRestarting();
            } else if (code == 504) {
                stat.requestTimeout();
                LoggerUtils.debugLog.warn("SplashScraper - Timeout expired {}", initialLink);
            } else if (code == 200) {
                var body = extractResponseBode(response);
                handleSuccessfulResponseBody(body);
            } else if (code == 400) {
                var body = extractResponseBode(response);
                handle400ResponseBody(body);
                stat.responseFailCode();
                return;
            } else {
                stat.responseFailCode();
            }
            stat.requestSucceeded();
            response.close();
        }

        private String extractResponseBode(Response response) throws IOException {
            var responseBody = response.body();
            if (responseBody == null) {
                throw new ScraperConnectionException("Response body is absent");
            }
            return responseBody.string();
        }

        private void handle400ResponseBody(String responseBody) {
            var splashResponse = gson.fromJson(responseBody, Splash400Response.class);
            if (splashResponse.getType().equals("ScriptError")) {
                throw new SplashScriptExecutionException(splashResponse.getInfo());
            }
            LoggerUtils.debugLog.error("SplashScraper - Unexpected 400 HTTP {}", responseBody);
        }

        private void handleSuccessfulResponseBody(String responseBody) {
            var splashResponse = gson.fromJson(responseBody, SplashResponse.class);
            var site = splashResponse.getUrl();
            finalLink = new Link(site);
            if (!isSameSite()) return;
            logRedirect();
            if (!call.isCanceled()) {
                consumer.accept(new Page(new Html(splashResponse.getHtml(), finalLink), initialLink));
                stat.siteScraped();
            }
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
                LoggerUtils.debugLog.info(
                        String.format("SplashScraper - Tried to redirect from %s to site %s", initialLink, finalLink)
                );
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
                LoggerUtils.debugLog.info(
                        String.format("SplashScraper - Redirect from %s to %s", initialLink, finalLink)
                );
            }
        }

        private void handleExceptionOnResponse(Exception e) {
            failedPages.add(new FailedPage(e, initialLink));
            if (e.getClass().equals(HtmlLanguageException.class)) {
                LoggerUtils.debugLog.info("SplashScraper - Wrong html language {}", initialLink.toString());
                stat.responseRejected();
            } if (e.getClass().equals(WrongFormedLinkException.class)) {
                LoggerUtils.consoleLog.error("SplashScraper - {}", e.getMessage());
                LoggerUtils.debugLog.error("SplashScraper - {}", e.getMessage(), e);
            } else {
                LoggerUtils.debugLog.error("SplashScraper - Exception while handling response, site {}",
                        initialLink.toString(), e);
                stat.responseException();
            }
        }

        private void handleSplashRestarting() {
            scheduledToRetry.incrementAndGet();
            var delay = getDelay(context.getRetryCount());
            if (delay == -1) {
                scheduledToRetry.decrementAndGet();
                throw new SplashNotRespondingException();
            } else {
                retryExecutor.schedule(this::retry, delay, TimeUnit.MILLISECONDS);
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
            var delay = 0;
            if (retryCount == 0) {
                delay = SPLASH_RESTART_TIME;
            } else if (retryCount < SPLASH_IS_UNAVAILABLE_RETRIES) {
                delay = SPLASH_RETRY_TIMEOUT;
            } else {
                delay = -1;
            }
            return delay;
        }

        private void retry() {
            synchronized (calls) {
                if (!call.isCanceled()) {
                    var newCall = call.clone();
                    calls.add(newCall);
                    newCall.enqueue(new SplashCallback(context.getForNewRetry()));
                }
            }
            scheduledToRetry.decrementAndGet();
            stat.requestRetried();
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
