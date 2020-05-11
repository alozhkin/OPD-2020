package crawler;

import utils.Link;

import java.util.Collection;

public interface LinkFilter {
    /**
     * Filter links relying on internal implementation
     *
     * @param links links to be filtered
     * @param currentLink from that page all links were taken
     * @return filtered links
     */
    Collection<Link> filter(Collection<Link> links, Link currentLink);

    /**
     * Filter links relying on internal implementation
     *
     * @param links to be filtered
     * @param currentLink from that page all links were taken
     * @param initialLink first link until redirects
     * @return filtered links
     */
    Collection<Link> filter(Collection<Link> links, Link currentLink, Link initialLink);
}
