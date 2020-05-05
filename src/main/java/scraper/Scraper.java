package scraper;

import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.function.Consumer;

public interface Scraper {
    void cancelAll();
    void scrapeAsync(Link link, Consumer<Html> consumer);
    void scrapeSync(Link link, Consumer<Html> consumer);
    int scrapingSitesCount();
    void shutdown();
}
