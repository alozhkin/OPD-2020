package selenium;

import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.util.Collection;

public class WebDriverManagerTest {

    @Test
    public void testGetDynamicContent() {
        String chromeDriverPath = "C:\\Users\\JekaJops\\IntelliJIDEAProjects\\words_extractor\\src\\main\\resources\\drivers\\chromedriver1.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        String url = "localhost/main.html";
        WebDriverManager webDriverManager = new WebDriverManager();
        webDriverManager.connect(new Link(url));
        BySet bySet = new BySet();
        bySet.builder().addTagNames("div", "button");
        Collection<Html> htmls = webDriverManager.getDynamicContent(bySet);
        for (Html html : htmls) {
            System.out.println(html);
        }
        //TODO
    }

}
