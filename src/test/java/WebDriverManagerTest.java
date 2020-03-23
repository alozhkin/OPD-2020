import org.junit.jupiter.api.Test;
import selenium.BySet;
import selenium.WebDriverManager;
import utils.Html;

import java.util.Collection;

public class WebDriverManagerTest {

    @Test
    public void testGetDynamicContent(){
        String url = "";
        WebDriverManager webDriverManager = new WebDriverManager(url);
        BySet bySet = new BySet();
        bySet.getBuilder().addTagNames("div", "button");
        Collection<Html> htmls = webDriverManager.getDynamicContent(bySet);
        //TODO
    }
}
