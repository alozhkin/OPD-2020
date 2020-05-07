package spider;

import database.Database;
import logger.LoggerUtils;
import logger.Statistic;
import scraper.SplashScraper;
import splash.ConnectionException;
import splash.DefaultSplashRequestFactory;
import splash.SplashNotRespondingException;
import utils.CSVParser;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Spider {
    // in seconds
    private static final int DOMAIN_TIMEOUT = 40;
    // after that number of fails in a row program stops
    private static final int CONNECT_FAILS = 50;

    private final ContextFactory contextFactory;
    private final Database database;
    private final Set<String> scrapedDomains = new HashSet<>();

    private int connectFailsCount = 0;

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

        try {
            for (Link domain : domains) {
                var fixed = domain.fixWWW().getHost();
                if (scrapedDomains.contains(fixed)) {
                    LoggerUtils.debugLog.info("Spider - Skip domain because is it already scraped " + domain);
                    LoggerUtils.consoleLog.info("Skip domain because is it already scraped " + domain);
                    continue;
                }
                scrapedDomains.add(fixed);
                Statistic.reset();
                var context = contextFactory.createContext();
                Set<String> allWords = ConcurrentHashMap.newKeySet();
                var scraper = new SplashScraper(requestFactory);
                var future = domainExec.submit(() -> new DomainTask(domain, context, scraper, allWords).scrapeDomain());
                try {
                    future.get(DOMAIN_TIMEOUT, TimeUnit.SECONDS);
                    connectFailsCount = 0;
                } catch (TimeoutException e) {
                    future.cancel(true);
                    LoggerUtils.debugLog.error("Spider - Waiting too long for scraping site " + domain);
                    LoggerUtils.consoleLog.error("Waiting too long for scraping site " + domain);
                } catch (ExecutionException e) {
                    var exClass = e.getCause().getClass();
                    if (exClass.equals(ConnectionException.class)) {
                        LoggerUtils.debugLog.error("DomainTask - Request failed " + domain, e);
                        LoggerUtils.consoleLog.error("Request failed " + domain + " " + e.getMessage());
                        ++connectFailsCount;
                        if (connectFailsCount == CONNECT_FAILS) {
                            throw new ConnectionException("Too many connect fails");
                        }
                    } else  {
                        throw e;
                    }
                }
                LoggerUtils.consoleLog.info(Statistic.string() + " site "  + domain);
                LoggerUtils.debugLog.info(Statistic.string() + " site "  + domain);
                dbExec.submit(new DatabaseTask(database, domain, allWords)::run);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtils.debugLog.error("Spider - Interrupted", e);
        } catch (ExecutionException e) {
            if (e.getCause().getClass().equals(SplashNotRespondingException.class)) {
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
            SplashScraper.shutdown();
            LoggerUtils.debugLog.info("Spider - Resources were closed");
        }
    }
}
