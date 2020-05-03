package spider;

import crawler.Crawler;
import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import extractor.DefaultExtractor;
import extractor.DefaultWordFilter;
import extractor.Extractor;

public class DefaultContextFactory implements ContextFactory {
    private Crawler crawler;
    private Extractor extractor;

    @Override
    public Context createContext() {
        if (crawler == null) {
            crawler = new DefaultCrawler();
        }
        if (extractor == null) {
            extractor = new DefaultExtractor();
        }

        var linkFilter = new DefaultLinkFilter();
        linkFilter.addDomain();

        return new DefaultContext(crawler, extractor, linkFilter, new DefaultWordFilter());
    }
}
