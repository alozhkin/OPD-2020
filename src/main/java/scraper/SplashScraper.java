package scraper;

import logger.LoggerUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import splash.DefaultSplashRequestContext;
import splash.SplashRequestFactory;
import utils.Html;
import utils.Link;

import java.io.EOFException;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SplashScraper implements Scraper {
    private final SplashRequestFactory rendererRequestFactory;
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();
    private final AtomicBoolean isSplashRestarting = new AtomicBoolean(false);
    //in millies
    private final int SPLASH_RESTART_TIME = 3000;

    public SplashScraper(SplashRequestFactory rendererRequestFactory) {
        this.rendererRequestFactory = rendererRequestFactory;
    }

    @Override
    public void scrape(Link link, Consumer<Html> consumer) {
        while (isSplashRestarting.get()) {
            isSplashRestarting.set(false);
            try {
                Thread.sleep(SPLASH_RESTART_TIME);
            } catch (InterruptedException ignored) {}
        }
        makeRequestToHtmlRenderer(link, consumer);
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
        httpClient.dispatcher().executorService().shutdown();
    }

    private void makeRequestToHtmlRenderer(Link link, Consumer<Html> consumer) {
        var request = rendererRequestFactory.getRequest(new DefaultSplashRequestContext.Builder(link).build());
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
            var html = new Html(responseBody.string(), link);
            if (!call.isCanceled()) {
                consumer.accept(html);
            }
        } catch (IOException e) {
            LoggerUtils.debugLog.error("SplashScraper - Connection failed " + link, e);
        }
    }

    private void handleFail(Call call, IOException e, Link link, Consumer<Html> consumer) {
        LoggerUtils.consoleLog.error("SplashScraper - Request failed " + call.request().url().toString(), e);
        calls.remove(call);
        if (e.getClass().equals(EOFException.class)) {
            retry(call, link, consumer);
        }
    }

    private void retry(Call call, Link link, Consumer<Html> consumer) {
        var newCall = call.clone();
        calls.add(newCall);
        newCall.enqueue(new SplashCallbackRetry(link, consumer));
    }

    private void retryOnce(Call call, Link link, Consumer<Html> consumer) {
        var newCall = call.clone();
        calls.add(newCall);
        newCall.enqueue(new SplashCallback(link, consumer));
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
