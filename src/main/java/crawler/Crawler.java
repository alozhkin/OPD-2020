package crawler;

import utils.Html;
import utils.Link;

import java.util.Collection;
import java.util.Set;

public interface Crawler {
    Collection<Link> crawl(Html html);
}
