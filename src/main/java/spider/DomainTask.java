package spider;

import logger.LoggerUtils;
import scraper.Scraper;
import utils.Link;

import java.util.concurrent.*;

public class DomainTask {
    private final Context context;
    private final Link domain;
    private final Scraper scraper;
    private final BlockingQueue<Link> linkQueue = new LinkedBlockingDeque<>();

    DomainTask(Context context, Scraper scraper, Link domain) {
        this.context = context;
        this.domain = domain;
        this.scraper = scraper;
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
        return scraper.scrapingSitesCount() != 0 || !linkQueue.isEmpty();
    }

    void checkIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    void scrapeNextLink() throws InterruptedException {
        var link = linkQueue.poll(500, TimeUnit.MILLISECONDS);
        if (link != null) {
            scraper.scrape(link, new SiteTask(context, linkQueue)::run);
        }
    }

    void handleInterruption() {
        scraper.cancelAll();
    }
}
