package selenium;

import diff_match_patch.DiffMatchPatch;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

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
        options.addArguments("--headless", "--disable-gpu");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
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
        int border = 1000; // Ќе окончательное решение. —корее всего предел нужно определ€ть исход€ из страницы.
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
        Element html = Jsoup.parse(currentHtml.toString(), currentLink.toString()).body();
        BySet bySet = new BySet();
        bySet = scan(html, bySet);
        return bySet;
    }

    BySet scan(@NotNull Element html, @NotNull BySet set) {
        set.addTagNames(html.tagName());
            for (int i = 0; i < html.childrenSize(); i++) {
                if (!html.tagName().equals("")) {
                    set.addTagNames(html.tagName());
                }
                set.addAll(scan(html.child(i), set));
            }
        return set;
    }

    public void quit() {
        driver.quit();
    }
}
