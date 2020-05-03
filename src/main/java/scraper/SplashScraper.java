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
import java.util.function.Consumer;

public class SplashScraper implements Scraper {
    private final SplashRequestFactory rendererRequestFactory;
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build();

    public SplashScraper(SplashRequestFactory rendererRequestFactory) {
        this.rendererRequestFactory = rendererRequestFactory;
    }

    @Override
    public void scrape(Link link, Consumer<Html> consumer) {
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
        call.enqueue(new SplashCallback(link, consumer));
    }

    //todo внутренняя ссылка не увеличивает размер объекта сильно?
    private class SplashCallback implements Callback {
        private final Link link;
        private final Consumer<Html> consumer;

        public SplashCallback(Link link, Consumer<Html> consumer) {
            this.link = link;
            this.consumer = consumer;
        }

        //todo fail support
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            LoggerUtils.consoleLog.error("SplashScraper - Request failed " + call.request().url().toString(), e);
            calls.remove(call);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) {
            handleResponse(response, call, link, consumer);
            calls.remove(call);
        }

        private void handleResponse(Response response, Call call, Link link, Consumer<Html> consumer) {
            try (response) {
                // splash container restarting
                // будет посылать запросы пока не надоест
                if (response.code() == 503) {
                    var newCall = call.clone();
                    calls.add(newCall);
                    newCall.enqueue(new SplashCallback(link, consumer));
                } else if (response.code() == 504) {
                    LoggerUtils.debugLog.error("SplashScraper - Timeout expired " + link);
                } else if (response.code() == 200) {
                    var responseBody = response.body();
                    if (responseBody == null) {
                        throw new IOException("Response body is absent");
                    }
                    var html = new Html(responseBody.string(), link);
                    if (!call.isCanceled()) {
                        consumer.accept(html);
                    }
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                LoggerUtils.debugLog.error("SplashScraper - Connection failed " + link, e);
            }
        }
    }
}
