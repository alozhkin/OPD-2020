package selenium;

import extractor.DefaultExtractor;
import extractor.Extractor;
import org.junit.Test;
import util.HTML;

public class SeleniumTestsJava implements SeleniumTestsInterface {
    String url = "https://project.spbstu.ru";
    WebDriverLauncher webDriverLauncher = new WebDriverLauncher(url);
    Extractor extractor = new DefaultExtractor();
    @Override
    @Test
    public void testNewSourceAfterClickingElement() {
        String msg1 = "Some new text in HTML after clicking";
        String msg2 = "Nothing changed";
        HTML source = webDriverLauncher.getHTMLSource();
        webDriverLauncher.clickSomeElements("button");
        HTML newSource = webDriverLauncher.getHTMLSource();
        checkChanges(newSource.size() > source.size(), msg1, msg2);
        webDriverLauncher = new WebDriverLauncher(url);
        newSource = webDriverLauncher.clickAllElementsToGetNewSource("button");
        checkChanges(!newSource.isEmpty(), msg1, msg2);
    }

    private void testClick(String url, String tagName) {

    }

    private void checkChanges(boolean condition, String msg1, String msg2) {
        if (condition) {
            System.out.println(msg1);
        } else {
            System.out.println(msg2);
        }
    }


}
