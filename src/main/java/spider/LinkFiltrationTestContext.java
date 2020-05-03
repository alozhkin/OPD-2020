package spider;

import crawler.Crawler;
import crawler.LinkFilter;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class LinkFiltrationTestContext implements Context {
    private final Crawler crawler;
    private final LinkFilter linkFilter;
    private final Collection<Link> accepted = new HashSet<>();
    private final Collection<Link> all = new HashSet<>();

    public LinkFiltrationTestContext(Crawler crawler, LinkFilter linkFilter) {
        this.crawler = crawler;
        this.linkFilter = linkFilter;
    }

    @Override
    public Collection<Link> crawl(Html html) {
        return crawler.crawl(html);
    }

    @Override
    public Collection<Link> filterLinks(Collection<Link> links, Link domain) {
        all.addAll(links);
        var filtered = linkFilter.filter(links, domain);
        accepted.addAll(filtered);
        return filtered;
    }

    // we don't need words extraction in link filtration tests, so we ignore it
    @Override
    public Collection<String> extract(Html html) {
        return new ArrayList<>();
    }

    // we don't need words filtration in link filtration tests, so we ignore it
    @Override
    public Collection<String> filterWords(Collection<String> words) {
        return new ArrayList<>();
    }

    public Collection<Link> getAccepted() {
        return accepted;
    }

    public Collection<Link> getAll() {
        return all;
    }
}
