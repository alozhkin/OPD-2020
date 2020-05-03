package scraper;

import utils.Html;
import utils.Link;

import java.util.function.Consumer;

public interface Scraper {
    void scrape(Link link, Consumer<Html> consumer);
    int scrapingSitesCount();
    void cancelAll();
}
