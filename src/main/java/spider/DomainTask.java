package spider;

import logger.LoggerUtils;
import scraper.Scraper;
import splash.ConnectionException;
import splash.SplashNotRespondingException;
import utils.Link;

import java.util.Set;
import java.util.concurrent.*;

public class DomainTask {
    private final Context context;
    private final Link domain;
    private final Scraper scraper;
    private final BlockingQueue<Link> linkQueue = new LinkedBlockingDeque<>();
    private final Set<String> resultWords;
    private int numberOfScrapedLinks = 1;

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
            if (numberOfScrapedLinks == 1) {
                findOut();
            }
        } catch (InterruptedException e) {
            handleInterruption();
            LoggerUtils.debugLog.error("Domain Task - Interrupted " + domain);
        } finally {
            LoggerUtils.debugLog.info("Domain Task - Stop executing site " + domain);
        }
    }

    private void findOut() {
        var failedSite = scraper.getFailedSites().get(0);
        if (failedSite != null) {
            var e = failedSite.getException();
            var exClass = e.getClass();
            if (exClass.equals(SplashNotRespondingException.class)) {
                LoggerUtils.debugLog.error("Spider - " + e.getMessage(), e);
                LoggerUtils.consoleLog.error(e.getMessage());
            } else if (exClass.equals(ConnectionException.class)) {
                throw (ConnectionException) e;
            } else if (exClass.equals(HtmlLanguageException.class)) {
                LoggerUtils.debugLog.error("DomainTask - Wrong html language " + domain);
                LoggerUtils.consoleLog.error("Wrong html language " + domain);
            } else {
                LoggerUtils.consoleLog.error("Domain ex", e);
            }
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
        scraper.scrape(link, new SiteTask(context, linkQueue, resultWords)::consumeHtml);
    }

    private void scrapeNextLink() throws InterruptedException {
        var link = linkQueue.poll(500, TimeUnit.MILLISECONDS);
        if (link != null) {
            scraper.scrape(link, new SiteTask(context, linkQueue, resultWords)::consumeHtml);
        }
        numberOfScrapedLinks++;
    }

    private void handleInterruption() {
        scraper.cancelAll();
    }
}
