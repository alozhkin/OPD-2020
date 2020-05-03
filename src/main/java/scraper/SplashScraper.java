package scraper;

import logger.LoggerUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import splash.SplashRequestFactory;
import splash.DefaultSplashRequestContext;
import utils.Html;
import utils.Link;

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

    void makeRequestToHtmlRenderer(Link link, Consumer<Html> consumer) {
        var request = rendererRequestFactory.getRequest(new DefaultSplashRequestContext.Builder(link).build());
        var call = httpClient.newCall(request);
        calls.add(call);
        call.enqueue(new Callback() {
            //todo fail support
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(call.request().url() + " " + e.toString());
                calls.remove(call);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                handleResponse(response, call, link, consumer);
                calls.remove(call);
            }
        });
    }

    void handleResponse(Response response, Call call, Link link, Consumer<Html> consumer) {
        try (response) {
            //todo 503, 504 support
            if (response.code() != 200) {
                throw new IOException();
            }
            //todo redirect support
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
}
