package spider;

import crawler.Crawler;
import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import extractor.DefaultExtractor;
import extractor.DefaultWordFilter;
import extractor.Extractor;
import scraper.DefaultScraper;
import scraper.Scraper;

public class DefaultContextFactory implements ContextFactory {
    private Scraper scraper;
    private Crawler crawler;
    private Extractor extractor;

    @Override
    public Context createContext() {
        if (scraper == null) {
            scraper = new DefaultScraper();
        }
        if (crawler == null) {
            crawler = new DefaultCrawler();
        }
        if (extractor == null) {
            extractor = new DefaultExtractor();
        }

        var linkFilter = new DefaultLinkFilter();
        linkFilter.addDomain();

        return new DefaultContext(scraper, crawler, extractor, linkFilter, new DefaultWordFilter());
    }
}
