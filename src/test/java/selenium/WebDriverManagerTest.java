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
        // инициализируем и запускаем WireMockServer
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        // в функции stubFor указываем тип запроса, url
        WireMock.stubFor(
                get(
                        urlEqualTo("/first_button"))
                        // в функции willReturn конфигурируем ответ (отправляем немного html)
                        .willReturn(
                                aResponse().withBody("<div id=\"worked\">Worked</div>")));
    }

    @AfterEach
    void quitMock() {
        wireMockServer.shutdown();
    }

    String getSitePath(String siteName){
        String projectPath = System.getProperty("project.path");
        return "file://" + projectPath + "/src/test/resources/DriverTestRes/"+siteName;
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

}
