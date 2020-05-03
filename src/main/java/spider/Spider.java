package spider;

import database.Database;
import logger.LoggerUtils;
import scraper.SplashScraper;
import splash.SplashRequestFactory;
import utils.CSVParser;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class Spider {
    private static final int DOMAIN_TIMEOUT = 240;

    private final ContextFactory contextFactory;
    private final Database database;

    public Spider(ContextFactory contextFactory, Database database) {
        this.contextFactory = contextFactory;
        this.database = database;
    }

    public void scrapeFromCSVFile(String input, String output) {
        var csvParser = new CSVParser();
        csvParser.parse(input);
        List<Link> domains = csvParser.getLinks();
        scrapeDomains(domains);
    }

    public void scrapeDomains(Collection<Link> domains) {
        var numberOfThreads = Integer.parseInt(System.getProperty("threads.number"));
        var exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        var domainExec = Executors.newSingleThreadScheduledExecutor();
        var dbExec = Executors.newSingleThreadScheduledExecutor();

        try {
            for (Link domain : domains) {
                var context = contextFactory.createContext();
                var factory = new SplashRequestFactory();
                var scraper = new SplashScraper(factory);
                var future = domainExec.submit(() -> new DomainTask(domain, context, scraper).scrapeDomain());
                try {
                    future.get(DOMAIN_TIMEOUT, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    LoggerUtils.debugLog.error("Main - Waiting too long for scraping site " + domain);
                }
                dbExec.submit(new DatabaseTask(database, domain, new HashSet<>())::run);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtils.debugLog.error("Main - Interrupted", e);
        } catch (Exception e) {
            LoggerUtils.debugLog.error("Main - Failed", e);
        } finally {
            LoggerUtils.debugLog.info("Main - Completed");
            exec.shutdown();
            try {
                exec.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LoggerUtils.debugLog.error("Main - Interrupted", e);
            }
            domainExec.shutdown();
            try {
                domainExec.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LoggerUtils.debugLog.error("Main - Interrupted", e);
            }
            dbExec.shutdown();
            LoggerUtils.debugLog.info("Main - Resources were closed");
        }
    }
}
