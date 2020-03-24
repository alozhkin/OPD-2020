import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scraper.DefaultScraper;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScraperTest {
    private DefaultScraper scraper;

    @BeforeEach
    void initScraper() {
        scraper = new DefaultScraper();
    }

    @Test
    void shouldFindNewWords() throws IOException {
        var html1 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/1.html")), new Link(""));
        var html2 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/2.html")), new Link(""));
        var actual = scraper.getNewWords(html1, html2);
        var expected = Set.of("new", "very", "useful", "and", ",", ">", "content");
        assertEquals(expected, new HashSet<>(actual));
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void shouldNotFindNewWords() throws IOException {
        var html1 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/2.html")), new Link(""));
        var html2 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/2.html")), new Link(""));
        var actual = scraper.getNewWords(html1, html2);
        assertEquals(0, actual.size());
    }

    @Test
    void shouldWorkFast() throws IOException {
        long start = System.currentTimeMillis();
        var html1 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/1.html")), new Link(""));
        var html2 = new Html(Files.readString(Paths.get("src/test/resources/wikipedia.html")), new Link(""));
        for (int i = 0; i < 1000; i++) {
            scraper.getNewWords(html1, html2);
        }
        long timeSpend = System.currentTimeMillis() - start;
        assertTrue(10000 > timeSpend);
    }
}
