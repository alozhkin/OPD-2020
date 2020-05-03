package spider;

import logger.LoggerUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import splash.SplashRequestContext;
import splash.HtmlRendererRequestFactory;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class DomainTask {
    private final Context context;
    private final BlockingQueue<Link> linkQueue;
    private final Link domain;
    private final OkHttpClient httpClient;
    private final HtmlRendererRequestFactory rendererRequestFactory;
    private final Set<Call> calls = ConcurrentHashMap.newKeySet();
    private final Set<String> allWords = new HashSet<>();
    private Link currentLink;

    DomainTask(Context context,
               BlockingQueue<Link> linkQueue,
               Link domain,
               OkHttpClient httpClient,
               HtmlRendererRequestFactory rendererRequestFactory) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.domain = domain;
        this.httpClient = httpClient;
        this.rendererRequestFactory = rendererRequestFactory;
    }

    void findTo() {
        LoggerUtils.debugLog.info("Domain Task - Start executing site " + domain);
        try {
            linkQueue.add(domain);
            while (areAllLinksScraped()) {
                checkIfInterrupted();
                scrapeNextLink();
            }
        } catch (InterruptedException e) {
            LoggerUtils.debugLog.error("Domain Task - Interrupted " + domain, e);
            handleInterruption();
        } finally {
            LoggerUtils.debugLog.info("Domain Task - Stop executing site " + domain);
        }
    }

    // order is important
    boolean areAllLinksScraped() {
        return httpClient.dispatcher().runningCallsCount() != 0 || !linkQueue.isEmpty();
    }

    void checkIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    void scrapeNextLink() throws InterruptedException {
        var link = linkQueue.poll(500, TimeUnit.MILLISECONDS);
        if (link != null) {
            currentLink = link;
            makeRequestToHtmlRenderer();
        }
    }

    void makeRequestToHtmlRenderer() {
        var request = rendererRequestFactory.getRequest(new SplashRequestContext.Builder(currentLink).build());
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
                handleResponse(response, call);
                calls.remove(call);
            }
        });
    }

    void handleResponse(Response response, Call call) {
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
            var html = new Html(responseBody.string(), currentLink);
            if (!call.isCanceled()) {
                var siteTask = new SiteTask(context, currentLink, linkQueue);
                var words = siteTask.run(html);
                allWords.addAll(words);
            }
        } catch (IOException e) {
            LoggerUtils.debugLog.error("Domain Task - Connection failed " + currentLink, e);
        }
    }

    void handleInterruption() {
        calls.forEach(Call::cancel);
    }
}
