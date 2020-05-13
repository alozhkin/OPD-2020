package spider;

import database.Database;
import utils.CSVParser;
import utils.Link;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

import static logger.LoggerUtils.consoleLog;
import static logger.LoggerUtils.debugLog;
import static ui.ConsoleUI.*;

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
        try {
            csvParser.parse(input);
        } catch (IOException e) {
            consoleLog.error("Spider - Failed to scrape from CSV file: ", e);
            debugLog.error("Spider - Failed to scrape from CSV File: ", e);
            return;
        }
        List<Link> domains = csvParser.getLinks();
        scrapeDomains(domains);
        database.exportDataToCSV(output);
        pb.step();
    }

    public void scrapeDomains(Collection<Link> domains) {
        var numberOfThreads = Integer.parseInt(System.getProperty("threads.number"));
        var exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        var cs = new ExecutorCompletionService<Collection<String>>(exec);
        var domainExec = Executors.newSingleThreadScheduledExecutor();
        var dbExec = Executors.newSingleThreadScheduledExecutor();
        pb.maxHint(domains.size() + 1);

        try {
            for (Link domain : domains) {
                var linkQueue = new LinkedBlockingDeque<Link>();
                var context = contextFactory.createContext();
                var allWords = new HashSet<String>();
                var future = domainExec.submit(() -> new DomainTask(context, linkQueue, cs, domain).findTo(allWords));
                try {
                    future.get(DOMAIN_TIMEOUT, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    future.cancel(true);
                    debugLog.error("Main - Waiting too long for scraping site " + domain);
                }
                dbExec.submit(new DatabaseTask(database, domain, allWords)::run);
                pb.step();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            debugLog.error("Main - Interrupted", e);
        } catch (Exception e) {
            debugLog.error("Main - Failed", e);
        } finally {
            debugLog.info("Main - Completed");
            exec.shutdown();
            try {
                exec.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                debugLog.error("Main - Interrupted", e);
            }
            domainExec.shutdown();
            try {
                domainExec.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                debugLog.error("Main - Interrupted", e);
            }
            dbExec.shutdown();
            debugLog.info("Main - Resources were closed");
        }
    }
}
