package scraper;

import com.google.gson.Gson;
import logger.LoggerUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import splash.DefaultSplashRequestContext;
import splash.SplashIsNotRespondingException;
import splash.SplashRequestFactory;
import utils.Html;
import utils.Link;

import java.io.EOFException;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SplashScraper implements Scraper {
    private final SplashRequestFactory renderReqFactory;
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();
    private final AtomicBoolean isSplashRestarting = new AtomicBoolean(false);
    //in millis
    private final int SPLASH_RESTART_TIME = 6000;
    private final int SPLASH_RETRY_TIMEOUT = 500;
    private final int SPLASH_IS_UNAVAILABLE_RETRIES = 5;

    public SplashScraper(SplashRequestFactory renderReqFactory) {
        this.renderReqFactory = renderReqFactory;
    }

    @Override
    public void scrape(Link link, Consumer<Html> consumer) {
        if (isSplashRestarting.get()) {
            if (!tryToMakeRequest(link, consumer, SPLASH_RESTART_TIME)) {
                for (int i = 0; i < SPLASH_IS_UNAVAILABLE_RETRIES; i++) {
                    if (tryToMakeRequest(link, consumer, SPLASH_RETRY_TIMEOUT)) return;
                }
                throw new SplashIsNotRespondingException();
            }
        } else {
            makeRequestToHtmlRenderer(link, consumer);
        }
    }

    @Override
    public int scrapingSitesCount() {
        return httpClient.dispatcher().runningCallsCount();
    }

    @Override
    public void cancelAll() {
        calls.forEach(Call::cancel);
    }

    @Override
    public void shutdown() {
        calls.forEach(Call::cancel);
        try {
            httpClient.dispatcher().executorService().shutdown();
        } catch (RejectedExecutionException e) {
            LoggerUtils.debugLog.error("SplashScraper - Site scraping rejected due to shutdown", e);
        }
    }

    private boolean tryToMakeRequest(Link link, Consumer<Html> consumer, int timeout) {
        if (pingSplash())  {
            makeRequestToHtmlRenderer(link, consumer);
            isSplashRestarting.set(false);
            return true;
        } else {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ignored) {}
            return false;
        }
    }

    private boolean pingSplash() {
        var request = renderReqFactory.getPingRequest(new DefaultSplashRequestContext.Builder().build());
        var call = httpClient.newCall(request);
        try {
            var code = call.execute().code();
            return code == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private void makeRequestToHtmlRenderer(Link link, Consumer<Html> consumer) {
        var request = renderReqFactory.getRequest(new DefaultSplashRequestContext.Builder().setSiteUrl(link).build());
        var call = httpClient.newCall(request);
        calls.add(call);
        call.enqueue(new SplashCallbackRetry(link, consumer));
    }

    private int handleResponse(Response response, Call call, Link link, Consumer<Html> consumer) {
        // splash container restarting
        int code = response.code();
        if (code == 504) {
            LoggerUtils.debugLog.error("SplashScraper - Timeout expired " + link);
        } else if (code == 200) {
            consumeHtml(response, call, link, consumer);
        }
        return code;
    }

    private void consumeHtml(Response response, Call call, Link link, Consumer<Html> consumer) {
        try (response) {
            var responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body is absent");
            }
            var gson = new Gson();
            var splashResponse = gson.fromJson(responseBody.string(), SplashResponse.class);
            var url = new Link(splashResponse.getUrl());
            if (!url.getWithoutProtocol().equals(link.getWithoutProtocol())) {
                LoggerUtils.debugLog.info(String.format("Redirect from %s to %s", link, url));
                LoggerUtils.consoleLog.info(String.format("Redirect from %s to %s", link, url));
            }
            var html = new Html(splashResponse.getHtml(), url);
            if (!call.isCanceled() && hasRightLang(html)) {
                consumer.accept(html);
            }
        } catch (IOException e) {
            LoggerUtils.debugLog.error("SplashScraper - Connection failed " + link, e);
        }
    }

    private boolean hasRightLang(Html html) {
        var siteLangs = System.getProperty("site.langs");
        var htmlLang = html.getLang();
        if (htmlLang != null) {
            for (String siteLang : siteLangs.split(",")) {
                if (siteLang.contains(htmlLang) || htmlLang.contains(siteLang)) return true;
            }
        } else {
            return System.getProperty("ignore.html.without.lang").equals("false");
        }
        return false;
    }

    private void handleFail(Call call, IOException e, Link link, Consumer<Html> consumer) {
        if (!e.getMessage().equals("Canceled")) {
            LoggerUtils.consoleLog.error("SplashScraper - Request failed " + link);
            LoggerUtils.consoleLog.error("SplashScraper - Request failed " + link + " " + e.getMessage());
        }
        if (e.getClass().equals(EOFException.class)) {
            retry(call, link, consumer);
        }
        calls.remove(call);
    }

    private void retry(Call call, Link link, Consumer<Html> consumer) {
        var newCall = call.clone();
        calls.add(newCall);
        newCall.enqueue(new SplashCallbackRetry(link, consumer));
    }

    private void retryOnce(Call call, Link link, Consumer<Html> consumer) {
        try {
            Thread.sleep(SPLASH_RESTART_TIME);
            var newCall = call.clone();
            calls.add(newCall);
            newCall.enqueue(new SplashCallback(link, consumer));
        } catch (InterruptedException ignored) {}
    }

    private static class SplashResponse {
        private final String html;
        private final String url;

        public SplashResponse(String html, String url) {
            this.html = html;
            this.url = url;
        }

        public String getHtml() {
            return html;
        }

        public String getUrl() {
            return url;
        }
    }

    private class SplashCallback implements Callback {
        private final Link link;
        private final Consumer<Html> consumer;

        public SplashCallback(Link link, Consumer<Html> consumer) {
            this.link = link;
            this.consumer = consumer;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            handleFail(call, e, link, consumer);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            handleResponse(response, call, link, consumer);
            calls.remove(call);
        }
    }

    //todo внутренняя ссылка не увеличивает размер объекта сильно?
    private class SplashCallbackRetry implements Callback {
        private final Link link;
        private final Consumer<Html> consumer;

        public SplashCallbackRetry(Link link, Consumer<Html> consumer) {
            this.link = link;
            this.consumer = consumer;
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            handleFail(call, e, link, consumer);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            var code = handleResponse(response, call, link, consumer);
            if (code == 503) {
                isSplashRestarting.set(true);
                retryOnce(call, link, consumer);
            }
            calls.remove(call);
        }
    }
}
