package crawler;

import utils.Html;
import utils.Link;

import java.util.List;

public interface Crawler {
    List<Link> crawl(Html html);
}
