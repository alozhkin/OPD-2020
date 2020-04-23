package spider;

import crawler.Crawler;
import crawler.LinkFilter;
import scraper.Scraper;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class LinkFiltrationTestContext implements Context {
    private final Scraper scraper;
    private final Crawler crawler;
    private final LinkFilter linkFilter;
    private final Collection<Link> accepted = new HashSet<>();
    private final Collection<Link> all = new HashSet<>();

    public LinkFiltrationTestContext(Scraper scraper, Crawler crawler, LinkFilter linkFilter) {
        this.scraper = scraper;
        this.crawler = crawler;
        this.linkFilter = linkFilter;
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
        all.addAll(links);
        var filtered = linkFilter.filter(links, domain);
        accepted.addAll(filtered);
        return filtered;
    }

    @Override
    public Collection<String> extract(Html html) {
        return new ArrayList<>();
    }

    @Override
    public Collection<String> filterWords(Collection<String> words) {
        return new ArrayList<>();
    }

    @Override
    public void quit() {
        scraper.quit();
    }

    public Collection<Link> getAccepted() {
        return accepted;
    }

    public Collection<Link> getAll() {
        return all;
    }
}
