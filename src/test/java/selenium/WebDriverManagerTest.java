package selenium;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import config.ConfigurationUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WebDriverManagerTest {
    WireMockServer wireMockServer;

    @BeforeAll
    static void configure() {
        ConfigurationUtils.configure();
    }

    @BeforeEach
    void initMock() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.stubFor(
                get(
                        urlEqualTo("/first_button"))
                        .willReturn(
                                aResponse().withBody("<div id=\"worked\">Worked</div>")));
    }

    @AfterEach
    void quitMock() {
        wireMockServer.shutdown();
    }

    String getSitePath(String siteName) {
        String projectPath = System.getProperty("project.path");
        return "file://" + projectPath + "/src/test/resources/DriverTestRes/" + siteName;
    }

    @Test
    void testGetDynamicContentWithClicking1() {
        String url = getSitePath("index1.html");
        Link link = new Link(url);
        WebDriverManager webDriverManager = new WebDriverManager(link);
        BySet bySet = new BySet();
        bySet.addTagNames("div", "a", "button");
        Collection<String> words = webDriverManager.getDynamicContentWithClicking(bySet);
        Assertions.assertFalse(words.isEmpty());
        //TODO
    }

    @Test
    void testGetDynamicContentWithClicking2() {
        WebDriverManager webDriverManager = new WebDriverManager(new Link(getSitePath("index1.html")));
        Collection<String> dynamicContent = webDriverManager.getDynamicContentWithClicking();
        Collection<String> expected = new ArrayList<>();
        expected.add("");
        //TODO: add some expected content
        Assertions.assertEquals(expected, dynamicContent);
    }

    @Test
    void contentShouldBeDifferent() {
        WebDriverManager webDriverManager = new WebDriverManager();
        Link link = new Link(getSitePath("index1.html"));
        Html parsedHtml = webDriverManager.parseHtmlWithJsoup(link);
        webDriverManager.connect(link);
        Html dynamicHtml = webDriverManager.getCurrentHtml();
        Assertions.assertNotEquals(parsedHtml, dynamicHtml);
    }

    @Test
    void constructBySetTest() {
        BySet expectedBySet = new BySet().addTagNames(
                "a", "pre", "b", "code", "h1", "i", "h2", "h3", "h4",
                "script", "div", "p", "ul", "abbr", "li", "ol", "span", "ul", "br");
        Link link = new Link(getSitePath("index3.html"));
        WebDriverManager manager = new WebDriverManager(link);
        BySet actualBySet = manager.constructBySet();
        Assertions.assertEquals(expectedBySet, actualBySet);
    }

    @Test
    void TestsConnectionErrors() {
        Html emptyHtml = new Html("", new Link(""));
        WebDriverManager webDriverManager = new WebDriverManager(new Link("https://key-seo.com/404"));
        Assertions.assertEquals(emptyHtml, webDriverManager.parseHtml());
        Assertions.assertEquals(emptyHtml, webDriverManager.parseHtmlWithJsoup());
        webDriverManager.setCurrentLink(new Link("https://github.com/fsdtygdsfbz4er"));
        Assertions.assertEquals(emptyHtml, webDriverManager.parseHtml());
        Assertions.assertEquals(emptyHtml, webDriverManager.parseHtmlWithJsoup());
    }
}
