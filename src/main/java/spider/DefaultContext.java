package spider;

import crawler.Crawler;
import crawler.LinkFilter;
import extractor.Extractor;
import extractor.WordFilter;
import scraper.Scraper;
import utils.Html;
import utils.Link;

import java.util.Collection;

public class DefaultContext implements Context {
    private final Scraper scraper;
    private final Crawler crawler;
    private final LinkFilter linkFilter;
    private final Extractor extractor;
    private final WordFilter wordFilter;

    DefaultContext(Scraper scraper,
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

    @Override
    public Html scrape(Link site) {
        return scraper.scrape(site);
    }

    @Override
    public Collection<Link> crawl(Html html) {
        return crawler.crawl(html);
    }

    @Override
    public Collection<Link> filterLinks(Collection<Link> links, Link domain) {
        return linkFilter.filter(links, domain);
    }

    @Override
    public Collection<String> extract(Html html) {
        return extractor.extract(html);
    }

    @Override
    public Collection<String> filterWords(Collection<String> words) {
        return wordFilter.filter(words);
    }

    @Override
    public void quit() {
        scraper.quit();
    }
}
