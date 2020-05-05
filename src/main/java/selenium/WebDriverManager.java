package selenium;

import diff_match_patch.DiffMatchPatch;
import logger.LoggerUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
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
        Connection connection = Jsoup.connect(currentLink.toString());
        try {
            Html parsedHtml = new Html(connection.get().toString(), currentLink);
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) e;
                LoggerUtils.debugLog.info("HTTP status code = " + se.getStatusCode()
                        + " ; URL - " + currentLink.toString() + " ; Exception : " + e);
            } else e.printStackTrace();
            return new Html("", new Link(""));
        }
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

    public Collection<String> getDynamicContentWithClicking() {
        return getDynamicContentWithClicking(constructBySet());
    }

    public Collection<String> getDynamicContentWithClicking(BySet bySet) {
        Collection<String> result = new ArrayList<>();
        List<WebElement> elements = new ArrayList<>();
        int i = 0;
        int border = 1000;
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
                //Ignored, because we can't know which tag would be interactable.
                //We just want to find some interactable elements and we just skip the other.
            }
        }
        return result;
    }

    public Html parseHtmlWithJsoup(Link link) {
        Connection connection = Jsoup.connect(link.getAbsoluteURL());
        Html parsedHtml;
        try {
            parsedHtml = new Html(connection.get().toString(), link);
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) e;
                LoggerUtils.debugLog.info("HTTP status code = " + se.getStatusCode()
                        + " ; URL - " + link + " ; Exception : " + e);
            } else e.printStackTrace();
            return new Html("", new Link(""));
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
        Element htmlElement = Jsoup.parse(currentHtml.toString(), currentLink.toString()).body();
        bySet = filter(scan(htmlElement, bySet));
        return bySet;
    }

    private BySet scan(@NotNull Element htmlElement, @NotNull BySet set) {
        set.addTagNames(htmlElement.tagName());
        for (int i = 0; i < htmlElement.childrenSize(); i++) {
            if (!htmlElement.tagName().equals("")) {
                set.addTagNames(htmlElement.tagName());
            }
        }
        if (htmlElement.childrenSize() != 0) {
            for (int i = 0; i < htmlElement.childrenSize(); i++) {
                set.addAll(scan(htmlElement.child(i), set));
            }
        }
        return set;
    }

    private BySet filter(BySet bySet) {
        By[] unnecessary = new By[]{
                By.tagName("body"),
                By.tagName("html"),
                //TODO: We need to find and add another unnecessary tags
        };
        for (By unBy : unnecessary) {
            while (bySet.contains(unBy)) {
                bySet.remove(unBy);
            }
        }
        return bySet;
    }

    public void quit() {
        driver.quit();
    }
}
