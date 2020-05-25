package spider;

import logger.LoggerUtils;
import scraper.Scraper;
import scraper.ScraperConnectionException;
import scraper.ScraperFailException;
import utils.Link;

import java.net.ConnectException;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Class that is fully responsible for scrape all pages from domain and put them into {@link DomainTask#resultWords}
 */
public class DomainTask {
    private final Context context;
    private final Link domain;
    private final Scraper scraper;
    private final BlockingQueue<Link> linkQueue = new LinkedBlockingDeque<>();
    private final Set<String> resultWords;
    private int numberOfScrapedLinks = 1;

    /**
     * @param domain to be scraped
     * @param context to be handed to SiteTask
     * @param scraper to scrape
     * @param resultWords to get all words
     */
    DomainTask(Link domain, Context context, Scraper scraper, Set<String> resultWords) {
        this.domain = domain;
        this.context = context;
        this.scraper = scraper;
        this.resultWords = resultWords;
    }

    /**
     * Gets all words from website
     * <p>
     * Go through all pages on site and give them to {@link Scraper}.
     * {@link Scraper} gets link and gives html,
     * {@link PageTask} gives words for database and links for {@link Scraper}.
     * <p>
     * Rethrows exception if domain (first link) failed, else ignore.
     */
    void scrapeDomain() {
        LoggerUtils.debugLog.info("Domain Task - Start executing site {}", domain);
        try {
            handleDomain();
        } catch (InterruptedException e) {
            handleInterruption();
        } finally {
            LoggerUtils.debugLog.info("Domain Task - Stop executing site {}", domain);
        }
    }

    private void handleDomain() throws InterruptedException {
        scrapeFirstLink(domain);
        while (areAllLinksScraped()) {
            checkIfInterrupted();
            scrapeNextLink();
        }
        if (numberOfScrapedLinks == 1) {
            checkIfScraperThrowException();
        }
    }

    private void scrapeFirstLink(Link link) {
        scraper.scrape(link, new PageTask(context, linkQueue, resultWords)::handlePage);
    }

    // order is important
    private boolean areAllLinksScraped() {
        return scraper.scrapingPagesCount() != 0 || !linkQueue.isEmpty();
    }

    private void checkIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private void scrapeNextLink() throws InterruptedException {
        var link = linkQueue.poll(200, TimeUnit.MILLISECONDS);
        if (link != null) {
            scraper.scrape(link, new PageTask(context, linkQueue, resultWords)::handlePage);
            numberOfScrapedLinks++;
        }
    }

    private void checkIfScraperThrowException() {
        var failedPages = scraper.getFailedPages();
        if (!failedPages.isEmpty()) {
            var failedPage = failedPages.get(0);
            if (failedPage != null) {
                handleException(failedPage.getException());
            }
        }
    }

    private void handleException(Exception e) {
        var exClass = e.getClass();
        if (exClass.equals(ConnectException.class)) {
            throw new ScraperConnectionException(e);
        } else if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new ScraperFailException(e);
        }
    }

    private void handleInterruption() {
        scraper.cancelAll();
    }
}
