package scraper;

import utils.Html;
import utils.Link;

public interface Scraper {
    Html scrape(Link site);
}
