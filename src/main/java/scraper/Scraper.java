package scraper;

import spider.FailedPage;
import spider.Page;
import utils.Link;

import java.util.List;
import java.util.function.Consumer;

/**
 * Class that is responsible for scraping html from web page
 */
public interface Scraper {
    /**
     * Cancel scraping pages which are being processed
     */
    void cancelAll();

    /**
     * @return all pages that were not scraped due to error with additional information
     */
    List<FailedPage> getFailedPages();

    /**
     * Follows link, extracts html and gives it to consumer with all required info
     *
     * @param link web page to be scraped
     * @param consumer consumer of html and additional information
     */
    void scrape(Link link, Consumer<Page> consumer);

    /**
     * @return number of pages which are being processed
     */
    int scrapingPagesCount();
}
