package selenium;

import config.ConfigurationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;

public class WebDriverManagerTest {

    @BeforeAll
    static void init() {
        ConfigurationUtils.configure();
    }

    @Test
    void testGetDynamicContentWithClicking1() {
        String url = "";
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
        WebDriverManager webDriverManager = new WebDriverManager(new Link(""));
        Collection<String> dynamicContent = webDriverManager.getDynamicContentWithClicking();
        Collection<String> expected = new ArrayList<>();
        expected.add("");
        //TODO: add some expected content
        Assertions.assertEquals(expected, dynamicContent);
    }

    @Test
    void contentShouldBeDifferent() {
        WebDriverManager webDriverManager = new WebDriverManager();
        Link link = new Link("");
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
        Link link = new Link("https://jsoup.org");
        WebDriverManager manager = new WebDriverManager(link);
        BySet actualBySet = manager.constructBySet();
        Assertions.assertEquals(expectedBySet, actualBySet);
    }

}
