package scraper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class used by SplashScraper to keep statistic
 */
public class Statistic {
    private final AtomicInteger requestsSended = new AtomicInteger(0);
    private final AtomicInteger requestsSucceeded = new AtomicInteger(0);
    private final AtomicInteger requestsFailed = new AtomicInteger(0);
    private final AtomicInteger requestsRetried = new AtomicInteger(0);
    private final AtomicInteger requestsTimeout = new AtomicInteger(0);
    private final AtomicInteger sitesScraped = new AtomicInteger(0);
    private final AtomicInteger responsesRejected = new AtomicInteger(0);
    private final AtomicInteger responsesException = new AtomicInteger(0);
    private final AtomicInteger responsesWithHTTPFailCode = new AtomicInteger(0);

    public int getRequestsSended() {
        return requestsSended.get();
    }

    public int getRequestSucceeded() {
        return requestsSucceeded.get();
    }

    public int getRequestsFailed() {
        return requestsFailed.get();
    }

    public int getRequestsRetried() {
        return requestsRetried.get();
    }

    public int getRequestsTimeout() {
        return requestsTimeout.get();
    }

    public int getSitesScraped() {
        return sitesScraped.get();
    }

    public int getResponsesException() {
        return responsesException.get();
    }

    public int getResponsesRejected() {
        return responsesRejected.get();
    }

    public int getHTTPFail() {
        return responsesWithHTTPFailCode.get();
    }

    public void requestSended() {
        requestsSended.incrementAndGet();
    }

    public void requestSucceeded() {
        requestsSucceeded.incrementAndGet();
    }

    public void requestFailed() {
        requestsFailed.incrementAndGet();
    }

    public void requestRetried() {
        requestsRetried.incrementAndGet();
    }

    public void requestTimeout() {
        requestsTimeout.incrementAndGet();
    }

    public void siteScraped() {
        sitesScraped.incrementAndGet();
    }

    public void responseException() {
        responsesException.incrementAndGet();
    }

    public void responseRejected() {
        responsesRejected.incrementAndGet();
    }

    public void responseFailCode() {
        responsesWithHTTPFailCode.incrementAndGet();
    }

    @Override
    public String toString() {
        return String.format(
                "Requests sended %d, request retried %d, requests succeeded %d, requests failed %d, \n"
                        + "request timeout %d, pages scraped %d, exceptions %d, responses rejected %d, responses" +
                        " status code fail %d",
                getRequestsSended(),
                getRequestsRetried(),
                getRequestSucceeded(),
                getRequestsFailed(),
                getRequestsTimeout(),
                getSitesScraped(),
                getResponsesException(),
                getResponsesRejected(),
                getHTTPFail()
        );
    }
}
