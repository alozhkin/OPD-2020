package crawler;

import utils.Link;

import java.util.Collection;

public interface LinkFilter {
    Collection<Link> filter(Collection<Link> links, Link currentLink);
    Collection<Link> filter(Collection<Link> links, Link currentLink, Link initialLink);
}
