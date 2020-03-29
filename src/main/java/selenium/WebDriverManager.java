package selenium;

import diff_match_patch.DiffMatchPatch;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebDriverManager {
    private Link currentLink;
    private WebDriver driver;
    private Html currentHtml;
    private static final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
    private static Set<WebDriver> drivers = ConcurrentHashMap.newKeySet();

    public WebDriverManager() {
        this(null);
    }

    public WebDriverManager(Link link) {
        init(link);
    }

    public void init(Link link) {
        driver = initDriver();
        if (link != null) {
            connect(link);
        }
    }

    private static WebDriver initDriver() {
        var options = new ChromeOptions();
        options.addArguments("--headless");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        var driver = new ChromeDriver(options);
        drivers.add(driver);
        return driver;
    }

    public void connect(Link link) {
        this.currentLink = link;
        driver.get(link.getAbsoluteURL());
        currentHtml = parseHtml();
    }

    public Html parseHtml() {
        return new Html(driver.getPageSource(), new Link(driver.getCurrentUrl()));
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

    public Collection<String> getDynamicContent() {
        Html html1 = parseHtmlWithJsoup();
        Html html2 = parseHtml();
        return getNewWords(html1, html2);
    }

    public Collection<String> getDynamicContentWithClicking(BySet bySet) {
        Collection<String> result = new ArrayList<>();
        List<WebElement> elements = new ArrayList<>();
        int i = 0;
        int border = 1000; // Не окончательное решение. Скорее всего предел нужно определять исходя из страницы.
        while (i < border) {
            for (By by : bySet) {
                elements.addAll(driver.findElements(by));
            }
            if (elements.isEmpty()) {
                connect(currentLink);
                continue;
            }
            WebElement element = elements.get(i);
            i++;
            try {
                element.click();
                result.addAll(getNewWords(currentHtml, parseHtml()));
            } catch (ElementNotInteractableException | StaleElementReferenceException ignored) {
            }

        }


        return result;
    }

    public Html parseHtmlWithJsoup(Link link) {
        Connection connection = Jsoup.connect(link.getAbsoluteURL());
        Html parsedHtml = new Html("", new Link(""));
        try {
            parsedHtml = new Html(connection.get().toString(), link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedHtml;
    }
    public Html parseHtmlWithJsoup() {
        return parseHtmlWithJsoup(currentLink);
    }

    public Collection<String> getNewWords(Html html1, Html html2) {
        var a = Jsoup.parse(html1.toString()).text();
        var b = Jsoup.parse(html2.toString()).text();
        return diffMatchPatch.getNewWords(" " + a + " ", " " + b + " ");
    }

    public BySet constructBySet() {
        BySet bySet = new BySet();
        //есть идея читать html и парсить все элементы которые там есть и создавать из этого bySet)
        //TODO
        return bySet;
    }

    public void quit() {
        driver.quit();
    }
}
