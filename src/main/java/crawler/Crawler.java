package crawler;

import utils.Html;
import utils.Link;

import java.util.Collection;

public interface Crawler {
    /**
     * Returns all links in html
     *
     * @param html
     * @return links in html
     */
    Collection<Link> crawl(Html html);
}
