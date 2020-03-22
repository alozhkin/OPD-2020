package main;

import crawler.Crawler;
import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import crawler.LinkFilter;
import database.Database;
import database.models.Word;
import extractor.DefaultExtractor;
import extractor.DefaultWordFilter;
import extractor.Extractor;
import extractor.WordFilter;
import utils.Html;
import utils.Link;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SiteParser {

    public static class Builder {
        BlockingQueue<Link> linkQueue;
        BlockingQueue<Html> HtmlQueue;
        Database db;
        String domain;

        Crawler crawler = new DefaultCrawler();
        Extractor extractor = new DefaultExtractor();
        WordFilter wordFilter = new DefaultWordFilter();
        LinkFilter linkFilter = new DefaultLinkFilter();

        public Builder(BlockingQueue<Link> linkQueue, BlockingQueue<Html> HtmlQueue, Database db,
                       String domain) {
            this.linkQueue = linkQueue;
            this.HtmlQueue = HtmlQueue;
            this.db = db;
            this.domain = domain;
        }

        public Builder crawler(Crawler value) {
            crawler = value;
            return this;
        }

        public Builder extractor(Extractor value) {
            extractor = value;
            return this;
        }

        public Builder wordFilter(WordFilter value) {
            wordFilter = value;
            return this;
        }

        public Builder linkFilter(LinkFilter value) {
            linkFilter = value;
            return this;
        }

        public SiteParser build() {
            return new SiteParser(this);
        }
    }

    private BlockingQueue<Link> linkQueue;
    private BlockingQueue<Html> HtmlQueue;
    private Database db;
    private String domain;
    private Crawler crawler;
    private Extractor extractor;
    private WordFilter wordFilter;
    private LinkFilter linkFilter;

    public SiteParser(Builder builder) {
        linkQueue = builder.linkQueue;
        HtmlQueue = builder.HtmlQueue;
        db = builder.db;
        domain = builder.domain;
        crawler = builder.crawler;
        extractor = builder.extractor;
        wordFilter = builder.wordFilter;
        linkFilter = builder.linkFilter;
    }

    private final Executor EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    public void start() {
        while (true) {
            try {
                var html = HtmlQueue.take();
                CompletableFuture<Set<Link>> crawlerFuture = CompletableFuture.supplyAsync(
                        () -> crawler.crawl(html),
                        EXECUTOR_SERVICE);
                crawlerFuture.thenAccept(result -> linkQueue.addAll(linkFilter.filter(result, domain)));
                CompletableFuture<Set<String>> extractorFuture = CompletableFuture.supplyAsync(
                        () -> extractor.extract(html),
                        EXECUTOR_SERVICE);
                extractorFuture.thenAccept(result -> {
                    var words = result.stream()
                            // TODO: Put website_id instead of domain.hashCode()
                            .map(word -> new Word(domain.hashCode(), word))
                            .collect(Collectors.toSet());
                    db.putWords(words);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
