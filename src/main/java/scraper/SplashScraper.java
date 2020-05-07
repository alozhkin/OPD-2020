package scraper;

import com.google.gson.Gson;
import logger.LoggerUtils;
import logger.Statistic;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import splash.ConnectionException;
import spider.FailedSite;
import splash.DefaultSplashRequestContext;
import splash.SplashNotRespondingException;
import splash.SplashRequestFactory;
import splash.SplashResponse;
import utils.Html;
import utils.Link;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SplashScraper implements Scraper {
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();
    //in millis
    private static final int SPLASH_RESTART_TIME = 6000;
    private static final int SPLASH_RETRY_TIMEOUT = 500;
    private static final int SPLASH_IS_UNAVAILABLE_RETRIES = 5;
    // если splash отрубится с 503 посреди домена, то ничего не предпримет, просто будет спамить в логгер,
    // если отрубится при загрузке первой страницы, то кинет exception.
    private static final Gson gson = new Gson();

    private final SplashRequestFactory renderReqFactory;
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService retryExecutor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger scheduledToRetry = new AtomicInteger(0);
    private final List<FailedSite> failedSites = new ArrayList<>();

    // а это thread-safe?
    private String domain;

    public SplashScraper(SplashRequestFactory renderReqFactory) {
        this.renderReqFactory = renderReqFactory;
    }

    public static void shutdown() {
        try {
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
    public void scrape(Link link, Consumer<Html> htmlConsumer) {
        var request = renderReqFactory.getRequest(new DefaultSplashRequestContext.Builder().setSiteUrl(link).build());
        var call = httpClient.newCall(request);
        calls.add(call);
        call.enqueue(new SplashCallback(new CallContext(link, htmlConsumer)));
        Statistic.requestSended();
    }

    @Override
    public int scrapingSitesCount() {
        // order is important
        return scheduledToRetry.get() + httpClient.dispatcher().runningCallsCount();
    }

    @Override
    public void cancelAll() {
        calls.forEach(Call::cancel);
    }

    @Override
    public List<FailedSite> getFailedSites() {
        return failedSites;
    }

    private void handleSplashRestarting(Call call, CallContext context) {
        scheduledToRetry.incrementAndGet();
        var delay = getDelay(context.getRetryCount());
        if (delay == -1) {
            throw new SplashNotRespondingException();
        } else {
            retryExecutor.schedule(() -> retry(call, context), delay, TimeUnit.MILLISECONDS);
        }
    }

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

    // а если call cancel?
    private void retry(Call call, CallContext context) {
        var newCall = call.clone();
        calls.add(newCall);
        newCall.enqueue(new SplashCallback(context.getForNewRetry()));
        scheduledToRetry.decrementAndGet();
        Statistic.requestRetried();
    }


    private class SplashCallback implements Callback {
        private final Link initialLink;
        private final Consumer<Html> consumer;
        private final CallContext context;
        private Link finalLink;
        private Call call;

        public SplashCallback(CallContext context) {
            this.initialLink = context.getLink();
            this.consumer = context.getConsumer();
            this.context = context;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            this.call = call;
            handleFail(e);
            failedSites.add(new FailedSite(e, initialLink));
            calls.remove(call);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            this.call = call;
            try {
                handleResponse(response);
            } catch (Exception e) {
                failedSites.add(new FailedSite(e, initialLink));
                Statistic.requestFailed();
            }
            calls.remove(call);
        }

        private void handleFail(IOException e) {
            Statistic.requestFailed();
            if (e.getClass().equals(EOFException.class)) {
                handleSplashRestarting(call, context);
            } else if (e.getMessage().equals("Canceled")) {
                LoggerUtils.debugLog.error("SplashScraper - Request canceled " + initialLink);
            } else if (e.getMessage().equals("executor rejected")) {
                LoggerUtils.debugLog.error("SplashScraper - Executor rejected " + initialLink);
            } else {
                LoggerUtils.debugLog.error("SplashScraper - Request failed " + initialLink, e);
            }
        }

        private void handleResponse(Response response) throws IOException {
            int code = response.code();
            if (code == 503 || code == 502) {
                handleSplashRestarting(call, context);
            } else if (code == 504) {
                Statistic.requestTimeout();
                LoggerUtils.debugLog.error("SplashScraper - Timeout expired " + initialLink);
            } else if (code == 200) {
                var responseBody = response.body();
                if (responseBody == null) {
                    throw new ConnectionException("Response body is absent");
                }
                handleResponseBody(responseBody.string());
            }
            Statistic.requestSucceeded();
            response.close();
        }

        private void handleResponseBody(String responseBody) {
            var splashResponse = gson.fromJson(responseBody, SplashResponse.class);
            finalLink = new Link(splashResponse.getUrl());
            if (!checkDomain()) return;
            checkRedirect();
            if (!call.isCanceled()) {
                consumer.accept(new Html(splashResponse.getHtml(), finalLink));
            }
        }

        private boolean checkDomain() {
            if (isDomainSuitable()) {
                return true;
            } else {
                LoggerUtils.debugLog.error(
                        String.format("SplashScraper - Tried to redirect from %s to site %s", initialLink, finalLink)
                );
                Statistic.responseRejected();
                return false;
            }
        }

        private boolean isDomainSuitable() {
            var scrapedUrlHostWithoutWWW = finalLink.fixWWW().getHost();
            if (domain == null) {
                domain = scrapedUrlHostWithoutWWW;
            } else {
                return scrapedUrlHostWithoutWWW.contains(domain);
            }
            return true;
        }

        private void checkRedirect() {
            var isRedirected = !finalLink.getWithoutProtocol().equals(initialLink.getWithoutProtocol());
            if (!isRedirected) {
                LoggerUtils.debugLog.info(
                        String.format("SplashScraper - Redirect from %s to %s", initialLink, finalLink)
                );
            }
        }
    }


    private static class CallContext {
        private final Link link;
        private final Consumer<Html> consumer;
        private final int retryCount;

        public CallContext(Link link, Consumer<Html> consumer) {
            this(link, consumer, 0);
        }

        public CallContext(Link link, Consumer<Html> consumer, int retryCount) {
            this.link = link;
            this.consumer = consumer;
            this.retryCount = retryCount;
        }

        public Link getLink() {
            return link;
        }

        public Consumer<Html> getConsumer() {
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
