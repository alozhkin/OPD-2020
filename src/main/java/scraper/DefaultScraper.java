package scraper;

import org.jsoup.Jsoup;
import utils.Html;
import utils.Link;

import java.io.IOException;

public class DefaultScraper implements Scraper {

    @Override
    public Html scrape(Link link) {
        // TODO
        try {
            return new Html(Jsoup.connect(link.toString()).get().toString(), link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
