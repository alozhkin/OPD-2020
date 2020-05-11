package scraper;

import spider.FailedSite;
import spider.Site;
import utils.Link;

import java.util.List;
import java.util.function.Consumer;

/**
 * Class that is responsible for scraping html from web page
 */
public interface Scraper {
    /**
     * Cancel scraping sites which are being processed
     */
    void cancelAll();

    /**
     *
     * @return all sites that were not scraped due to error
     */
    List<FailedSite> getFailedSites();

    /**
     * Extracts html from link and gives it to consumer with all required info
     *
     * @param link web page to be scraped
     * @param consumer consumes html
     */
    void scrape(Link link, Consumer<Site> consumer);

    /**
     *
     * @return number of sites which are being processed
     */
    int scrapingSitesCount();
}
