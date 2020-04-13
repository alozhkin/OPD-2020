package main;

import config.ConfigurationUtils;
import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import database.Database;
import extractor.DefaultExtractor;
import extractor.DefaultWordFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scraper.DefaultScraper;
import utils.CSVParser;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    public static AtomicLong submittedTasksCount = new AtomicLong(0);
    public static AtomicLong completedTaskCount = new AtomicLong(0);

    public static Logger debugLog = LoggerFactory.getLogger("FILE");
    public static Logger consoleLog = LoggerFactory.getLogger("STDOUT");

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ConfigurationUtils.configure();

        var csvParser = new CSVParser();
        csvParser.parse("src/main/resources/websites_data_short.csv");
        List<Link> domains = csvParser.getLinks();

        var scraper = new DefaultScraper();
        var crawler = new DefaultCrawler();
        var linkFilter = new DefaultLinkFilter();
        var extractor = new DefaultExtractor();
        var wordFilter = new DefaultWordFilter();
        var database = Database.newInstance();
        var linkQueue = new LinkedBlockingDeque<Link>();

        var numberOfThreads = Integer.valueOf(System.getProperty("threads.number"));
        var exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        var cs = new ExecutorCompletionService<Collection<String>>(exec);
        var dbExec = Executors.newSingleThreadExecutor();

        try {
            for (Link domain : domains) {
                var allWords = new HashSet<String>();
                cs.submit(new SiteTask(scraper, crawler, linkFilter, extractor, wordFilter, domain, linkQueue)::run);
                submittedTasksCount.incrementAndGet();
                // order is important
                while (completedTaskCount.get() - submittedTasksCount.get() != 0 || linkQueue.size() != 0) {
                    var link = linkQueue.poll(50, TimeUnit.MILLISECONDS);
                    if (link != null) {
                        cs.submit(new SiteTask(scraper, crawler, linkFilter, extractor, wordFilter, link, linkQueue)::run);
                        submittedTasksCount.incrementAndGet();
                    }
                    var wordsFuture = cs.poll(50, TimeUnit.MILLISECONDS);
                    if (wordsFuture != null) {
                        allWords.addAll(wordsFuture.get());
                    }
                }
                dbExec.submit(new DatabaseTask(database, domain, allWords)::run);
            }
        } finally {
            Main.debugLog.info("Main task completed");
            exec.shutdown();
            dbExec.shutdown();
            scraper.quit();
        }
    }
}
