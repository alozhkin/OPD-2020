package logger;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistic {
    private static final AtomicInteger requestsSended = new AtomicInteger(0);
    private static final AtomicInteger responsesReceived = new AtomicInteger(0);
    private static final AtomicInteger sitesScraped = new AtomicInteger(0);

    public static int getRequestsSended() {
        return requestsSended.get();
    }

    public static int getResponsesReceived() {
        return responsesReceived.get();
    }

    public static int getSitesScraped() {
        return sitesScraped.get();
    }

    public static void requestSended() {
        requestsSended.incrementAndGet();
    }

    public static void responseReceived() {
        responsesReceived.incrementAndGet();
    }

    public static void siteScraped() {
        sitesScraped.incrementAndGet();
    }

    public static void reset() {
        responsesReceived.set(0);
        requestsSended.set(0);
        sitesScraped.set(0);
    }
}
