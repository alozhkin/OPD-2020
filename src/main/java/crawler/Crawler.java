package crawler;

import util.HTML;
import util.Link;

import java.util.List;

public interface Crawler {
    List<Link> crawl(HTML html);
}
