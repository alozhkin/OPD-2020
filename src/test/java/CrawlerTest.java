import crawler.DefaultCrawler;
import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrawlerTest {
    @Test
    public void drillingUrlOutput() throws IOException {
        Html htmlSite = Html.fromFile(Path.of("src/test/resourcestelefort.spb.ru.html"));
        Set<String> InList = Set.of(
                "http://telefort.spb.ru/contacts.htm",
                "http://telefort.spb.ru/obj.pdf",
                "http://metrika.yandex.ru/stat/?id=6380740&from=informer",
                "https://pbx.telefort.spb.ru/",
                "http://telefort.spb.ru/partners.htm",
                "http://telefort.spb.ru/index.html",
                "http://telefort.spb.ru/vacancy.htm",
                "http://telefort.spb.ru/documents.htm"
        );
        Set<Link> editedInList = InList.stream().map(Link::new).collect(Collectors.toSet());
        assertEquals(editedInList,
                new DefaultCrawler().crawl(new Html(htmlSite.toString(), new Link("http://telefort.spb.ru/"))));
    }
}