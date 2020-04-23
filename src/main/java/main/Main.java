package main;

import config.ConfigurationUtils;
import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import database.Database;
import extractor.DefaultExtractor;
import extractor.DefaultWordFilter;
import logger.LoggerUtils;
import scraper.DefaultScraper;
import utils.CSVParser;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    // in seconds
    private static final int DOMAIN_TIMEOUT = 240;
    private static final String INPUT_PATH = "src/main/resources/websites_data_short.csv";
    private static final String OUTPUT_PATH = "export.csv";

    public static void main(String[] args) {
        start(INPUT_PATH, OUTPUT_PATH);
    }

    public static void start(String input, String output) {
        LoggerUtils.debugLog.info("Main - START");
        ConfigurationUtils.configure();

        var csvParser = new CSVParser();
        csvParser.parse(input);
        List<Link> domains = csvParser.getLinks();

        var context = new Context(
                new DefaultScraper(),
                new DefaultCrawler(),
                new DefaultExtractor()
        );

        var database = Database.newInstance();

        var numberOfThreads = Integer.parseInt(System.getProperty("threads.number"));
        var exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        var cs = new ExecutorCompletionService<Collection<String>>(exec);
        var domainExec = Executors.newSingleThreadScheduledExecutor();
        var dbExec = Executors.newSingleThreadScheduledExecutor();

        try {
            for (Link domain : domains) {
                var linkFilter = new DefaultLinkFilter();
                linkFilter.addDomain();
                var wordFilter = new DefaultWordFilter();
                context.setLinkFilter(linkFilter);
                context.setWordFilter(wordFilter);
                var linkQueue = new LinkedBlockingDeque<Link>();

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
            context.quit();
            LoggerUtils.debugLog.info("Main - Resources were closed");
        }
    }
}