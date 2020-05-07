package scraper;

import utils.Html;
import utils.Link;

import java.util.List;
import java.util.function.Consumer;

public interface Scraper {
    void cancelAll();
    void scrape(Link link, Consumer<Html> consumer);
    int scrapingSitesCount();
    List<FailedSite> getFailedSites();
}
