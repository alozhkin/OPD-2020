package logger;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistic {
    private static final AtomicInteger requestsSended = new AtomicInteger(0);
    private static final AtomicInteger requestsSucceeded = new AtomicInteger(0);
    private static final AtomicInteger requestsFailed = new AtomicInteger(0);
    private static final AtomicInteger requestsRetried = new AtomicInteger(0);
    private static final AtomicInteger requestsTimeout = new AtomicInteger(0);
    private static final AtomicInteger sitesScraped = new AtomicInteger(0);
    private static final AtomicInteger responsesRejected = new AtomicInteger(0);

    public static int getRequestsSended() {
        return requestsSended.get();
    }

    public static int getRequestSucceeded() {
        return requestsSucceeded.get();
    }

    public static int getRequestsFailed() {
        return requestsFailed.get();
    }

    public static int getRequestsRetried() {
        return requestsRetried.get();
    }

    public static int getRequestsTimeout() {
        return requestsTimeout.get();
    }

    public static int getSitesScraped() {
        return sitesScraped.get();
    }

    public static int getResponsesRejected() {
        return responsesRejected.get();
    }

    public static void requestSended() {
        requestsSended.incrementAndGet();
    }

    public static void requestSucceeded() {
        requestsSucceeded.incrementAndGet();
    }

    public static void requestFailed() {
        requestsFailed.incrementAndGet();
    }

    public static void requestRetried() {
        requestsRetried.incrementAndGet();
    }

    public static void requestTimeout() {
        requestsTimeout.incrementAndGet();
    }

    public static void siteScraped() {
        sitesScraped.incrementAndGet();
    }

    public static void responseRejected() {
        responsesRejected.incrementAndGet();
    }

    public static void reset() {
        requestsSended.set(0);
        requestsSucceeded.set(0);
        requestsFailed.set(0);
        requestsRetried.set(0);
        requestsTimeout.set(0);
        sitesScraped.set(0);
        responsesRejected.set(0);
    }

    public static String string() {
        return String.format(
                "Requests sended %d, request retried %d, requests succeeded %d, requests failed %d, "
                        + "request timeout %d, pages scraped %d, responses rejected %d",
                Statistic.getRequestsSended(),
                Statistic.getRequestsRetried(),
                Statistic.getRequestSucceeded(),
                Statistic.getRequestsFailed(),
                Statistic.getRequestsTimeout(),
                Statistic.getSitesScraped(),
                Statistic.getResponsesRejected()
        );
    }
}
