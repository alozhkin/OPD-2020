package scraper;

import org.openqa.selenium.WebDriver;
import utils.Html;
import utils.Link;

public interface Scraper {
    Html scrape(Link site);
    void quit();
}
