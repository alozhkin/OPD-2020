package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebDriverManager {
    private String url;
    private static WebDriver driver = new ChromeDriver();
    private Html currentHtml;

    public WebDriverManager() {
    }

    public WebDriverManager(String url) {
        connect(url);
    }

    public WebDriverManager(Link link) {
        connect(link.toString());
    }

    public WebDriverManager getNextWebsite(String url) {
        connect(url);
        return this;
    }

    public WebDriverManager getNextWebsite(Link link) {
        return getNextWebsite(link.toString());
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    private void connect(String url) {
        this.url = url;
        driver.get(url);
        currentHtml = getHtml();
    }

    public Html getHtml() {
        return new Html(driver.getPageSource(), new Link(url));
    }

    public Collection<Html> getDynamicContent(BySet bySet){
        Collection<WebElement> elements = new ArrayList<>();
        for (By by : bySet) {
            elements.addAll(driver.findElements(by));
        }
        BlockingQueue<Html> htmls = new ArrayBlockingQueue<>(1000);
        for (WebElement element : elements) {
            try {
                element.click();
                Html difference = getDifference(currentHtml, getHtml());
                if (!difference.toString().isEmpty()) {
                    htmls.add(difference);
                }
            } catch (ElementNotInteractableException enie) {
                //TODO
            }
        }
        return htmls;
    }

    private Html getDifference(Html html1, Html html2){
        Html result = new Html("", new Link(""));
        //TODO
        return result;
    }

}
