package scraper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import config.ConfigurationUtils;
import extractor.DefaultExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class ScraperTest {
    private DefaultScraper scraper;
    private WireMockServer wireMockServer;

    @BeforeEach
    void initTests() {
        scraper = new DefaultScraper();
        ConfigurationUtils.configure();
    }

    void initMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @Test
    void shouldFindNewWords() throws IOException {
        var html1 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/1.html")), Link.createEmptyLink());
        var html2 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/2.html")), Link.createEmptyLink());
        var actual = scraper.getNewWords(html1, html2);
        var expected = Set.of("new", "very", "useful", "and", ",", ">", "content");
        assertEquals(expected, new HashSet<>(actual));
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void shouldNotFindNewWords() throws IOException {
        var html1 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/2.html")), Link.createEmptyLink());
        var html2 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/2.html")), Link.createEmptyLink());
        var actual = scraper.getNewWords(html1, html2);
        assertEquals(0, actual.size());
    }

    @Test
    void shouldWorkFast() throws IOException {
        long start = System.currentTimeMillis();
        var html1 = new Html(Files.readString(Paths.get("src/test/resources/scraper_res/1.html")), Link.createEmptyLink());
        var html2 = new Html(Files.readString(Paths.get("src/test/resources/wikipedia.html")), Link.createEmptyLink());
        for (int i = 0; i < 1000; i++) {
            scraper.getNewWords(html1, html2);
        }
        long timeSpend = System.currentTimeMillis() - start;
        assertTrue(10000 > timeSpend);
    }

    @Test
    void shouldIgnoreHtmlOnWrongLanguage() {
        var html = scraper.scrape(Link.createFileLink(Paths.get("src/test/resources/scraper_res/wrong_language.html")));
        assertEquals(Html.emptyHtml(), html);
    }

    @Test
    void shouldNotIgnoreHtmlOnRightLanguage() {
        var html = scraper.scrape(Link.createFileLink(Paths.get("src/test/resources/scraper_res/right_language.html")));
        assertNotEquals(Html.emptyHtml(), html);
    }

    @Test
    @EnabledIfSystemProperty(named = "ignore.html.without.lang", matches = "true")
    void shouldIgnoreHtmlWithoutLanguage() {
        var html = scraper.scrape(Link.createFileLink(Paths.get("src/test/resources/scraper_res/without_lang.html")));
        assertEquals(Html.emptyHtml(), html);
    }

    @Test
    @EnabledIfSystemProperty(named = "ignore.html.without.lang", matches = "false")
    void shouldNotIgnoreHtmlWithoutLanguage() {
        var html = scraper.scrape(Link.createFileLink(Paths.get("src/test/resources/scraper_res/without_lang.html")));
        assertNotEquals(Html.emptyHtml(), html);
    }

    @Test
    void shouldBeAbleToHandleRedirect() throws IOException {
        initMockServer();
        var html = Html.fromFile(Path.of("src/test/resources/scraper_res/simple.html"));
        stubFor(
                get(
                        urlEqualTo("/redirect")
                ).willReturn(
                        permanentRedirect("/redirect_to")
                )
        );
        stubFor(
                get(
                        urlEqualTo("/redirect_to")
                ).willReturn(
                        aResponse().withBody(html.toString())
                )
        );
        var resHtml = scraper.scrape(new Link("localhost:8080/redirect"));
        var words = new DefaultExtractor().extract(resHtml);
        assertEquals(Set.of("test"), words);
    }
}
