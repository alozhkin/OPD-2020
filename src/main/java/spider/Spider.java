package spider;

import database.Database;
import logger.LoggerUtils;
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
        var cs = new ExecutorCompletionService<Collection<String>>(exec);
        var domainExec = Executors.newSingleThreadScheduledExecutor();
        var dbExec = Executors.newSingleThreadScheduledExecutor();

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
                    LoggerUtils.debugLog.error("Main - Waiting too long for scraping site " + domain);
                }
                dbExec.submit(new DatabaseTask(database, domain, allWords)::run);
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
