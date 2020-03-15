package crawler;

import utils.HTML;
import utils.Link;

import java.util.List;

public interface Crawler {
    List<Link> crawl(HTML html);
}
