package crawler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrawlerTest {
    Crawler crawler;

    @BeforeEach
    void init() {
        crawler = new DefaultCrawler();
    }

    @Test
    void shouldNotFailOnEmptyHtml() throws IOException {
        Set<String> inSet = Set.of(
                "http://telefort.spb.ru/contacts.htm",
                "http://telefort.spb.ru/obj.pdf",
                "http://metrika.yandex.ru/stat/?id=6380740&from=informer",
                "https://pbx.telefort.spb.ru/",
                "http://telefort.spb.ru/partners.htm",
                "http://telefort.spb.ru/index.html",
                "http://telefort.spb.ru/vacancy.htm",
                "http://telefort.spb.ru/documents.htm"
        );
        Set<Link> editedInSet = inSet.stream().map(Link::new).collect(Collectors.toSet());
        Html html = Html.fromFile(Path.of("src/test/resources/telefort.spb.ru.html"), new Link("http://telefort.spb.ru/"));
        assertEquals(editedInSet, crawler.crawl(html));
    }
}