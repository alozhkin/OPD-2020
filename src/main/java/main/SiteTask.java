package main;

import crawler.Crawler;
import crawler.LinkFilter;
import extractor.Extractor;
import extractor.WordFilter;
import scraper.Scraper;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class SiteTask {
    private Scraper scraper;
    private Crawler crawler;
    private LinkFilter linkFilter;
    private Extractor extractor;
    private WordFilter wordFilter;
    private Link link;
    private BlockingQueue<Link> linkQueue;

    public SiteTask(Scraper s,
                    Crawler c,
                    LinkFilter lF,
                    Extractor e,
                    WordFilter wF,
                    Link l,
                    BlockingQueue<Link> q) {
        scraper = s;
        crawler = c;
        linkFilter = lF;
        extractor = e;
        wordFilter = wF;
        link = l;
        linkQueue = q;
    }

    public Collection<String> run() {
        try {
            var html = scraper.scrape(link);
            var links = crawler.crawl(html);
            var filteredLinks = linkFilter.filter(links, link);
            linkQueue.addAll(filteredLinks);
            var words = extractor.extract(html);
            return wordFilter.filter(words);
        } catch (Exception e) {
            Main.consoleLog.error("SiteTask - Failed to run program: {}", e.toString());
            Main.debugLog.error("SiteTask - Failed to run program:", e);
            return new ArrayList<>();
        } finally {
            Main.debugLog.info("Site task completed");
            Main.completedTaskCount.incrementAndGet();
        }
    }
}