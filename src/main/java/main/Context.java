package main;

import crawler.Crawler;
import crawler.LinkFilter;
import extractor.Extractor;
import extractor.WordFilter;
import scraper.Scraper;
import utils.Html;
import utils.Link;

import java.util.Collection;

public class Context {
    private final Scraper scraper;
    private final Crawler crawler;
    private LinkFilter linkFilter;
    private final Extractor extractor;
    private WordFilter wordFilter;

    Context(Scraper scraper,
            Crawler crawler,
            Extractor extractor) {
        this(scraper, crawler, extractor, null, null);
    }

    Context(Scraper scraper,
            Crawler crawler,
            Extractor extractor,
            LinkFilter linkFilter,
            WordFilter wordFilter) {
        this.scraper = scraper;
        this.crawler = crawler;
        this.linkFilter = linkFilter;
        this.extractor = extractor;
        this.wordFilter = wordFilter;
    }

    void setLinkFilter(LinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }

    void setWordFilter(WordFilter wordFilter) {
        this.wordFilter = wordFilter;
    }

    Html scrape(Link site) {
        return scraper.scrape(site);
    }

    Collection<Link> crawl(Html html) {
        return crawler.crawl(html);
    }

    Collection<Link> filter(Collection<Link> links, Link domain) {
        return linkFilter.filter(links, domain);
    }

    Collection<String> extract(Html html) {
        return extractor.extract(html);
    }

    Collection<String> filter(Collection<String> words) {
        return wordFilter.filter(words);
    }

    void quit() {
        scraper.quit();
    }
}
