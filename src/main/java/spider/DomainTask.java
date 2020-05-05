package spider;

import logger.LoggerUtils;
import scraper.Scraper;
import utils.Link;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

public class DomainTask {
    private final Context context;
    private final Link domain;
    private final Scraper scraper;
    private final BlockingQueue<Link> linkQueue = new LinkedBlockingDeque<>();
    private final Set<String> resultWords;

    DomainTask(Link domain, Context context, Scraper scraper, Set<String> resultWords) {
        this.domain = domain;
        this.context = context;
        this.scraper = scraper;
        this.resultWords = resultWords;
    }

    // throws exceptions only on first link
    void scrapeDomain() {
        LoggerUtils.debugLog.info("Domain Task - Start executing site " + domain);
        try {
            scrapeFirstLink(domain);
            while (areAllLinksScraped()) {
                checkIfInterrupted();
                try {
                    scrapeNextLink();
                } catch (HtmlLanguageException ignored) {}
            }
        } catch (InterruptedException e) {
            handleInterruption();
            LoggerUtils.debugLog.error("Domain Task - Interrupted " + domain);
        } finally {
            LoggerUtils.debugLog.info("Domain Task - Stop executing site " + domain);
        }
    }

    // order is important
    private boolean areAllLinksScraped() {
        return scraper.scrapingSitesCount() != 0 || !linkQueue.isEmpty();
    }

    private void checkIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private void scrapeFirstLink(Link link) {
        scraper.scrapeSync(link, new SiteTask(context, linkQueue, resultWords)::consumeHtml);
    }

    private void scrapeNextLink() throws InterruptedException {
        var link = linkQueue.poll(500, TimeUnit.MILLISECONDS);
        if (link != null) {
            scraper.scrapeAsync(link, new SiteTask(context, linkQueue, resultWords)::consumeHtml);
        }
    }

    private void handleInterruption() {
        scraper.cancelAll();
    }
}
