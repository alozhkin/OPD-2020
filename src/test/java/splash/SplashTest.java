package splash;

import com.github.tomakehurst.wiremock.WireMockServer;
import config.ConfigurationUtils;
import extractor.DefaultExtractor;
import org.junit.jupiter.api.*;
import scraper.Scraper;
import scraper.SplashScraper;
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
    private Scraper scraper = new SplashScraper(new DefaultSplashRequestFactory());

    void initMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @BeforeAll
    static void configure() {
        ConfigurationUtils.configure();
    }

    @Test
    @Disabled
    void scrapeOnePageWithSplash() {
        scraper.scrape(new Link("http://www.zwickedelstahl.de"), System.out::println);
    }

    @Test
    @Disabled
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
        scraper.scrape(new Link("localhost:8080/redirect"), t -> {
            var words = new DefaultExtractor().extract(t.getHtml());
            assertEquals(Set.of("test"), words);
        });
    }

    @Test
    @Disabled
    public void ignorePics() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/scraper_res/ignore_pics.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/pic.png"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        scraper.scrape(new Link("http://localhost:8080/"), t -> {});
        while (scraper.scrapingPagesCount() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Disabled
    public void ignoreCSS() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/scraper_res/ignore_css.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/style.css"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        stubFor(get(urlEqualTo("/favicon.ico"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        scraper.scrape(new Link("http://localhost:8080/"), System.out::println);
        while (scraper.scrapingPagesCount() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Disabled
    public void ignoreFavicon() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/scraper_res/ignore_css.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/favicon.ico"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        scraper.scrape(new Link("http://localhost:8080/"), t -> {});
        while (scraper.scrapingPagesCount() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Disabled
    public void ignoreJS() throws IOException {
        initMockServer();
        stubFor(get(urlEqualTo("/"))
                .willReturn(
                        aResponse().withBody(
                                Html.fromFile(Paths.get("src/test/resources/scraper_res/ignore_js.html")).toString()
                        ).withHeader("content-type", "text/html; charset=UTF-8")
                )
        );
        stubFor(get(urlEqualTo("/e/analytics.js"))
                .willReturn(aResponse().withStatus(404).withFixedDelay(5000)));
        scraper.scrape(new Link("http://localhost:8080/"), t -> {});
        while (scraper.scrapingPagesCount() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

