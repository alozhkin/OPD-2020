package spider;

import crawler.Crawler;
import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import scraper.DefaultScraper;
import scraper.Scraper;

import java.util.ArrayList;
import java.util.List;

public class LinkFiltrationTestContextFactory implements ContextFactory {
    private final List<Context> contexts = new ArrayList<>();

    private Scraper scraper;
    private Crawler crawler;

    @Override
    public Context createContext() {
        if (scraper == null) {
            scraper = new DefaultScraper();
        }
        if (crawler == null) {
            crawler = new DefaultCrawler();
        }

        var linkFilter = new DefaultLinkFilter();
        linkFilter.addDomain();

        var context = new LinkFiltrationTestContext(scraper, crawler, linkFilter);
        contexts.add(context);
        return context;
    }

    public List<Context> getContexts() {
        return contexts;
    }
}
