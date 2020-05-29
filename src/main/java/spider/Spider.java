package spider;

import database.Database;
import scraper.*;
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
                if (checkDomainAlreadyWas() && checkDomainIsSuitable()) continue;
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
            handleScraperFail(e);
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

    // for no apparent reason Splash completely crashes at this website
    // issue (https://github.com/scrapinghub/splash/issues/985)
    // with similar stacktrace did not receive a response
    private boolean checkDomainIsSuitable() {
        String host = domain.fixWWW().getHost();
        return !host.equals("m-eppich.de") && !host.equals("seat.de");
    }

    private void handleDomainFuture(Future<?> future) throws InterruptedException {
        try {
            future.get(DOMAIN_TIMEOUT, TimeUnit.SECONDS);
            domainsFailsInARowCount = 0;
        } catch (TimeoutException e) {
            handleScraperTimeout(future);
        } catch (ExecutionException e) {
            var cause = e.getCause().getClass();
            if (cause.equals(SplashScriptExecutionException.class) || cause.equals(ScraperConnectionException.class)) {
                checkNumberOfScraperFails();
            } else if (cause.equals(SplashNotRespondingException.class)) {
                throw (SplashNotRespondingException) e.getCause();
            } else if (cause.equals(ScraperFailException.class)) {
                throw (ScraperFailException) e.getCause();
            }
            if (isScraperError(e.getCause())) {
                debugLog.error("Spider - Site {} processing failed due to {}", domain, cause.getSimpleName());
                consoleLog.error("Spider - Site {} processing failed due to {}", domain, cause.getSimpleName());
            }
        }
    }

    private void handleScraperTimeout(Future<?> future) {
        future.cancel(true);
        debugLog.warn("Spider - Stopped, waiting too long for scraping site {}", domain);
        consoleLog.warn("Spider stopped, waiting too long for scraping site {}", domain);
    }

    private void checkNumberOfScraperFails() {
        ++domainsFailsInARowCount;
        if (domainsFailsInARowCount == DOMAINS_FAILS) {
            throw new ScraperFailException(new TooManyFailsException());
        }
    }

    private void trackStatistic(Statistic statistic) {
        debugLog.info("Spider - {}, site {}", statistic.toString(), domain);
    }

    private boolean isScraperError(Throwable cause) {
        if (cause.getClass().equals(HtmlLanguageException.class)) return false;
        if (cause.getClass().equals(SplashScriptExecutionException.class)) {
            String error = ((SplashScriptExecutionException) cause).getInfo().getError();
            // webkit102 is thrown when pdf is loading
            return !(error.startsWith("network") || error.equals("webkit102"));
        }
        return true;
    }

    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleInterrupt(InterruptedException e) {
        Thread.currentThread().interrupt();
        debugLog.error("Spider - Interrupted", e);
    }

    private void handleScraperFail(Exception e) {
        var causeClass = e.getCause().getClass();
        if (causeClass.equals(TooManyFailsException.class)) {
            debugLog.error("Spider - Stopped, {}", e.getMessage());
            consoleLog.error("Spider stopped, {}", e.getMessage());
        } else {
            debugLog.error("Spider - Stopped, unexpected error", e);
            consoleLog.error("Spider stopped, unexpected error {}", causeClass.getSimpleName());
        }
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
}
