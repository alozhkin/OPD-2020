package crawler;

import utils.Html;
import utils.Link;

import java.util.Collection;

/**
 * Interface responsible for extracting links from html
 */
public interface Crawler {
    /**
     * Returns all links in html
     *
     * @param html html
     * @return links in html
     */
    Collection<Link> crawl(Html html);
}
