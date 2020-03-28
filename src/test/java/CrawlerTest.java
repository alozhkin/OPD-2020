import crawler.Crawler;
import crawler.DefaultCrawler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Html;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrawlerTest {
    Crawler crawler;

    @BeforeEach
    void init() {
        crawler = new DefaultCrawler();
    }

    @Test
    void shouldNotFailOnEmptyHtml() {
        assertEquals(0, crawler.crawl(Html.emptyHtml()).size());
    }
}
