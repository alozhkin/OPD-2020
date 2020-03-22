package crawler;

import utils.Html;
import utils.Link;

import java.util.Set;

public interface Crawler {
    Set<Link> crawl(Html html);
}
