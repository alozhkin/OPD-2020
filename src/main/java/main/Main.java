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

        var context = new Context(
                new DefaultScraper(),
                new DefaultCrawler(),
                new DefaultExtractor()
        );

        var database = Database.newInstance();
        var linkQueue = new LinkedBlockingDeque<Link>();

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

                var allWords = new HashSet<String>();
                var t = domainExec.submit(() -> new DomainTask(context, linkQueue, cs, domain).findTo(allWords));
                try {
                    t.get(1, TimeUnit.MINUTES);
                } catch (TimeoutException e) {
                    consoleLog.error("Waiting too long for scraping site " + domain);
                }
                dbExec.submit(new DatabaseTask(database, domain, allWords)::run);
            }
        } finally {
            Main.debugLog.info("Main task completed");
            exec.shutdown();
            dbExec.shutdown();
            context.quit();
        }
    }
}
