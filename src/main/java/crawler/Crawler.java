package crawler;

import utils.Html;
import utils.Link;

import java.util.Collection;

public interface Crawler {
    Collection<Link> crawl(Html html);
}
