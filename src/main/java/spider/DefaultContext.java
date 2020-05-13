package spider;

import crawler.Crawler;
import crawler.LinkFilter;
import extractor.Extractor;
import extractor.WordFilter;
import utils.Html;
import utils.Link;

import java.util.Collection;

/**
 * Simple class that does not change the algorithm and fully rely on preassigned behaviors
 */
public class DefaultContext implements Context {
    private final Crawler crawler;
    private final LinkFilter linkFilter;
    private final Extractor extractor;
    private final WordFilter wordFilter;

    DefaultContext(Crawler crawler,
            Extractor extractor,
            LinkFilter linkFilter,
            WordFilter wordFilter) {
        this.crawler = crawler;
        this.linkFilter = linkFilter;
        this.extractor = extractor;
        this.wordFilter = wordFilter;
    }

    @Override
    public Collection<Link> crawl(Html html) {
        return crawler.crawl(html);
    }

    @Override
    public Collection<Link> filterLinks(Collection<Link> links, Link currentLink, Link initialLink) {
        return linkFilter.filter(links, currentLink, initialLink);
    }

    @Override
    public Collection<String> extract(Html html) {
        return extractor.extract(html);
    }

    @Override
    public Collection<String> filterWords(Collection<String> words) {
        return wordFilter.filter(words);
    }
}
