package spider;

import utils.Html;
import utils.Link;

import java.util.Collection;

/**
 * Encapsulates all algorithms for html processing
 */
public interface Context {
    /**
     * Returns all links in html
     *
     * @param html html
     * @return links in html
     */
    Collection<Link> crawl(Html html);

    /**
     * Filter links
     *
     * @param links to be filtered
     * @param currentLink from that page all links were taken
     * @param initialLink first link until redirects
     * @return filtered links
     */
    Collection<Link> filterLinks(Collection<Link> links, Link currentLink, Link initialLink);

    /**
     * Returns all words from html
     *
     * @param html html
     * @return all words
     */
    Collection<String> extract(Html html);

    /**
     * Filters words
     *
     * @param words words to be filtered
     * @return filtered words
     */
    Collection<String> filterWords(Collection<String> words);
}
