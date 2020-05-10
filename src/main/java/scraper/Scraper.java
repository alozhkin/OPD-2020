package scraper;

import spider.FailedSite;
import spider.Site;
import utils.Link;

import java.util.List;
import java.util.function.Consumer;

public interface Scraper {
    void cancelAll();
    List<FailedSite> getFailedSites();
    void scrape(Link link, Consumer<Site> consumer);
    int scrapingSitesCount();
}
