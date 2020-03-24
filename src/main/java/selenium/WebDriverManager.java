package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;

public class WebDriverManager {
    private Link currentLink;
    private WebDriver driver;
    private Html currentHtml;

    public WebDriverManager() {
        this(null);
    }

    public WebDriverManager(Link link) {
        init(link);
    }

    public void init(Link link) {
        driver = new ChromeDriver();
        if (link != null) {
            connect(link);
        }
    }

    public void connect(Link link) {
        this.currentLink = link;
        driver.get(link.getAbsoluteURL());
        currentHtml = parseHtml();
    }

    public Html parseHtml() {
        return new Html(driver.getPageSource(), currentLink);
    }

    public void setCurrentLink(Link link) {
        currentLink = link;
    }

    public Link getCurrentLink() {
        return currentLink;
    }

    public void resetCurrentHtml() {
        currentHtml = parseHtml();
    }

    public Html getCurrentHtml() {
        return currentHtml;
    }

    public Collection<Html> getDynamicContent(BySet bySet) {
        Collection<WebElement> elements = new ArrayList<>();
        for (By by : bySet) {
            elements.addAll(driver.findElements(by));
        }
        ArrayList<Html> htmls = new ArrayList<>();
        for (WebElement element : elements) {
            element.click();
            Html difference = getDifference(currentHtml, parseHtml());
            if (difference != null) {
                htmls.add(difference);
            }
        }

        return htmls;
    }

    private Html getDifference(Html html1, Html html2) {
        return null;
    }

}
