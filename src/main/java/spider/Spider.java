package spider;

import database.Database;
import logger.LoggerUtils;
import scraper.SplashScraper;
import splash.DefaultSplashRequestFactory;
import splash.SplashIsNotRespondingException;
import utils.CSVParser;
import utils.Link;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Spider {
    // in seconds
    private static final int DOMAIN_TIMEOUT = 40;

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
        var domainExec = Executors.newSingleThreadScheduledExecutor();
        var dbExec = Executors.newSingleThreadScheduledExecutor();

        var requestFactory = new DefaultSplashRequestFactory();
        var scraper = new SplashScraper(requestFactory);

        try {
            for (Link domain : domains) {
                var context = contextFactory.createContext();
                Set<String> allWords = ConcurrentHashMap.newKeySet();
                var future = domainExec.submit(() -> new DomainTask(domain, context, scraper, allWords).scrapeDomain());
                try {
                    future.get(DOMAIN_TIMEOUT, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    LoggerUtils.debugLog.error("Spider - Waiting too long for scraping site " + domain);
                }
                dbExec.submit(new DatabaseTask(database, domain, allWords)::run);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtils.debugLog.error("Spider - Interrupted", e);
        } catch (ExecutionException e) {
            if (e.getCause().getClass().equals(SplashIsNotRespondingException.class)) {
                LoggerUtils.debugLog.error("Spider - " + e.getMessage(), e);
                LoggerUtils.consoleLog.error(e.getMessage());
            } else {
                LoggerUtils.debugLog.error("Spider - Failed", e);
            }
        } catch (Exception e) {
            LoggerUtils.debugLog.error("Spider - Failed", e);
        } finally {
            LoggerUtils.debugLog.info("Spider - Completed");
            //todo вспомнить почему здесь есть awaitTermination, а там нет.
            domainExec.shutdown();
            try {
                domainExec.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LoggerUtils.debugLog.error("Spider - Interrupted", e);
            }
            dbExec.shutdown();
            scraper.shutdown();
            LoggerUtils.debugLog.info("Spider - Resources were closed");
        }
    }
}
