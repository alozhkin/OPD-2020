package selenium;

import config.ConfigurationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.util.Collection;

public class WebDriverManagerTest {

    @BeforeAll
    static void init() {
        ConfigurationUtils.configure();
    }

    @Test
    void testGetDynamicContent() {
        String url = "https://www.brks.de";
        Link link = new Link(url);
        WebDriverManager webDriverManager = new WebDriverManager(link);
        BySet bySet = new BySet();
        bySet.addTagNames("div", "a", "button");
        Collection<String> words = webDriverManager.getDynamicContentWithClicking(bySet);
        Assertions.assertFalse(words.isEmpty());
        //TODO
    }

    @Test
    void contentShouldBeDifferent() {
        WebDriverManager webDriverManager = new WebDriverManager();
        Link link = new Link("https://albrecht-dill.de/");
        Html parsedHtml = webDriverManager.parseHtmlWithJsoup(link);
        webDriverManager.connect(link);
        Html dynamicHtml = webDriverManager.getCurrentHtml();
        Assertions.assertNotEquals(parsedHtml, dynamicHtml);
//        Collection<String> words = driverManager.getNewWords(parsedHtml, dynamicHtml);
//        for (String word : words) {
//            System.out.println(word);
//        }
    }

    @Test
    void constructBySetTest() {
        BySet expectedBySet = new BySet().addTagNames(
                "a", "pre", "b", "code", "h1", "i", "h2", "h3", "h4",
                "script", "div", "p", "ul", "abbr", "li", "ol", "span", "body", "ul", "br");

        Link link = new Link("https://jsoup.org");
        WebDriverManager manager = new WebDriverManager(link);
        BySet actualBySet = manager.constructBySet();
        Assertions.assertEquals(expectedBySet, actualBySet);

        expectedBySet = new BySet().addTagNames(
                "a", "br", "p", "ol", "h5", "h4", "li", "h3", "h2", "h1", "ul", "em", "nav",
                "div", "img", "footer", "canvas", "input", "header", "textarea", "body",
                "button", "label", "span", "article", "form", "strong", "script");

        link = new Link("https://albrecht-dill.de/");
        manager = new WebDriverManager(link);
        actualBySet = manager.constructBySet();
        Assertions.assertEquals(expectedBySet, actualBySet);
    }
}
