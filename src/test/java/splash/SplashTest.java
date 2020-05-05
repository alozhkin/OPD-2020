package splash;

package scraper;

import com.github.tomakehurst.wiremock.WireMockServer;
import config.ConfigurationUtils;
import database.Database;
import extractor.DefaultExtractor;
import logger.LoggerUtils;
import org.junit.jupiter.api.*;
import scraper.Scraper;
import scraper.SplashScraper;
import spider.Spider;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SplashTest {
    private WireMockServer wireMockServer;
    private Scraper scraper = new SplashScraper();

    void initMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @BeforeAll
    static void configure() {
        ConfigurationUtils.configure();
    }

    @Test
    void runSplash() {
        LoggerUtils.debugLog.info("SPLASH START");
        var spider = new Spider(new SplashContextFactory(), Database.createDummy());
        spider.scrapeDomains(Set.of(new Link("http://www.zwickedelstahl.de")));
    }

    @Test
    void scrapeOnePageWithSplash() {
        System.out.println(scraper.scrape(new Link("http://www.zwickedelstahl.de")));
    }

    @Test
    void scrapeOnePageWithChrome() {
        var defaultScraper = new DefaultScraper();
        defaultScraper.scrape(new Link("http://jsoup.org/apidocs/org/jsoup/Connection.Base.html"));
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


    @Test
    public void ignorePics() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/ignore_pics.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/pic.png"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(20000)));
        scraper.scrape(new Link("http://localhost:8080/"));
    }

    @Test
    public void ignoreCSS() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/ignore_css.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/style.css"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        stubFor(get(urlEqualTo("/favicon.ico"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        System.out.println(scraper.scrape(new Link("http://localhost:8080/")));
    }

    @Test
    public void ignoreFavicon() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/ignore_css.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/favicon.ico"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        scraper.scrape(new Link("http://localhost:8080/"));
    }

    @Test
    public void ignoreJS() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/ignore_js.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/e/analytics.js"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        scraper.scrape(new Link("http://localhost:8080/"));
    }

    @Test
    public void dod() {
        LoggerUtils.debugLog.info("SPLASH START");
        var spider = new Spider(new TestContextFactory(), Database.createDummy());
        spider.scrapeFromCSVFile("src/main/resources/websites_data.csv", "");
    }
}

