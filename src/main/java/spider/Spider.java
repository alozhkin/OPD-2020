package spider;

import database.Database;
import scraper.ScraperFailException;
import scraper.Statistic;
import scraper.SplashScraper;
import scraper.ScraperConnectionException;
import splash.DefaultSplashRequestFactory;
import splash.SplashNotRespondingException;
import splash.SplashScriptExecutionException;
import utils.CSVParser;
import utils.Link;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static logger.LoggerUtils.*;


/**
 * Class carrying out the main work of the program: turn domains into words inside database
 */
public class Spider {
    // in seconds
    private static final int DOMAIN_TIMEOUT = 15;
    // after that number of fails in a row program stops
    private static final int DOMAINS_FAILS = 10;

    private final ContextFactory contextFactory;
    private final Database database;
    private final Set<String> scrapedDomains = new HashSet<>();

    private int domainsFailsInARowCount = 0;
    private Link domain;
    private OnSpiderChangesListener listener;
    private Map<String, Integer> domainIds;

    public Spider(ContextFactory contextFactory, Database database) {
        this.contextFactory = contextFactory;
        this.database = database;
    }

    /**
     * Gets domains from csv file, extracts words and puts them inside database.
     * <p>
     * CSV file: "id";"company_id";"website";
     *
     * @param input path to CSV file with domains
     * @param output path to which the output file with words will be placed
     */
    public void scrapeFromCSVFile(String input, String output) {
        var csvParser = new CSVParser();
        try {
            csvParser.parse(input);
        } catch (IOException e) {
            consoleLog.error("Spider - Failed to scrape from CSV file: ", e);
            debugLog.error("Spider - Failed to scrape from CSV File: ", e);
            onFinished();
            return;
        }
        try {
            domainIds = csvParser.getDomainsIds();
            List<Link> domains = csvParser.getLinks();
            scrapeDomains(domains);
            if (database.exportDataToCSV(output)) {
                database.clearWebsites();
                database.clearWords();
            }
            onDataExported();
        } finally {
            onFinished();
        }
    }

    /**
     * Follows links, extracts words and puts them inside database.
     * <p>
     * Ignores repeated domains, domains are separated by host name without <i>"www"<i/>.
     *
     * @param domains to be scraped
     */
    public void scrapeDomains(Collection<Link> domains) {
        var domainExec = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService dbExec = Executors.newSingleThreadScheduledExecutor();
        var requestFactory = new DefaultSplashRequestFactory();
        onDomainsParsed(domains);

        try {
            for (Link d : domains) {
                domain = d;
                onDomainScraped();
                if (checkDomainAlreadyWas()) continue;
                var context = contextFactory.createContext();
                var scraper = new SplashScraper(requestFactory);
                Set<String> allWords = ConcurrentHashMap.newKeySet();
                var future = domainExec.submit(() -> new DomainTask(domain, context, scraper, allWords).scrapeDomain());
                handleDomainFuture(future);
                trackStatistic(scraper.getStatistic());
                dbExec.submit(new DatabaseTask(database, domain, allWords, domainIds)::run);
            }
        } catch (InterruptedException e) {
            handleInterrupt(e);
        } catch (ScraperFailException e) {
            handleScraperFail();
        } catch (SplashNotRespondingException e) {
            handleSplashNotResponding(e);
        } catch (Exception e) {
            debugLog.error("Spider - Failed", e);
        } finally {
            handleFinish(domainExec, dbExec);
        }
    }

    public void setListener(OnSpiderChangesListener listener) {
        this.listener = listener;
    }

    private boolean checkDomainAlreadyWas() {
        var fixed = domain.fixWWW().getHost();
        if (scrapedDomains.contains(fixed)) {
            debugLog.info("Spider - Skip domain because is it already scraped {}", domain);
            consoleLog.warn("Skip domain because is it already scraped {}", domain);
            return true;
        }
        scrapedDomains.add(fixed);
        return false;
    }

    private void handleDomainFuture(Future<?> future) throws InterruptedException, ExecutionException {
        try {
            future.get(DOMAIN_TIMEOUT, TimeUnit.SECONDS);
            domainsFailsInARowCount = 0;
        } catch (TimeoutException e) {
            handleScraperTimeout(future);
        } catch (ExecutionException e) {
            var exClass = e.getCause().getClass();
            if (exClass.equals(SplashScriptExecutionException.class)) {
                handleSplashExecutionFail((SplashScriptExecutionException) e.getCause());
            } else if (exClass.equals(ScraperConnectionException.class)) {
                handleConnectionFail((ScraperConnectionException) e.getCause());
            } else if (exClass.equals(SplashNotRespondingException.class)) {
                throw (SplashNotRespondingException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    private void handleScraperTimeout(Future<?> future) {
        future.cancel(true);
        debugLog.error("Spider - Stopped, waiting too long for scraping site {}", domain);
        consoleLog.error("Spider stopped, waiting too long for scraping site {}", domain);
    }

    private void handleSplashExecutionFail(SplashScriptExecutionException e) {
        debugLog.error("Spider - Request failed {} {} {}", domain, e.getClass().getSimpleName(), e.getInfo());
        consoleLog.error("Request failed {} {} {}", domain, e.getClass().getSimpleName(), e.getInfo().getType());
        handleScraperFail(e);
    }

    private void handleConnectionFail(ScraperConnectionException e) {
        debugLog.error("Spider - Request failed {} {}", domain, e.getClass().getSimpleName());
        consoleLog.error("Request failed {} {}", domain, e.getMessage());
        handleScraperFail(e);
    }

    private void handleScraperFail(Exception e) {
        ++domainsFailsInARowCount;
        if (domainsFailsInARowCount == DOMAINS_FAILS) {
            throw new ScraperFailException("Too many connect fails", e);
        }
    }

    private void trackStatistic(Statistic statistic) {
        consoleLog.info("{}, site {}", statistic.toString(), domain);
        debugLog.info("Spider - {}, site {}", statistic.toString(), domain);
    }

    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void onDomainsParsed(Collection<Link> domains) {
        if (listener != null) {
            listener.onDomainsParsed(domains);
        }
    }

    private void onDataExported() {
        if (listener != null) {
            listener.onDataExported();
        }
    }

    private void onDomainScraped() {
        if (listener != null) {
            listener.onDomainScraped();
        }
    }

    private void onFinished() {
        if (listener != null) {
            listener.onFinished();
        }
    }

    private void handleInterrupt(InterruptedException e) {
        Thread.currentThread().interrupt();
        debugLog.error("Spider - Interrupted", e);
    }

    private void handleScraperFail() {
        debugLog.error("Spider - Stopped, too many fails");
        consoleLog.error("Spider stopped, too many fails");
    }

    private void handleSplashNotResponding(SplashNotRespondingException e) {
        debugLog.error("Spider - {}", e.getMessage(), e);
        consoleLog.error(e.getMessage());
    }

    private void handleFinish(ScheduledExecutorService domainExec, ScheduledExecutorService dbExec) {
        closeResources(domainExec, dbExec);
        debugLog.info("Spider - {} sites were scraped", scrapedDomains.size());
        consoleLog.info("Spider - {} sites were scraped", scrapedDomains.size());
        debugLog.info("Spider - Completed");
    }

    private void closeResources(ScheduledExecutorService domainExec, ScheduledExecutorService dbExec) {
        shutdownExecutorService(dbExec);
        shutdownExecutorService(domainExec);
        SplashScraper.shutdown();
        debugLog.info("Spider - Resources were closed");
    }
}
