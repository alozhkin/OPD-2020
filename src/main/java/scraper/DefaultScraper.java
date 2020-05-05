package scraper;

import diff_match_patch.DiffMatchPatch;
import logger.LoggerUtils;
import org.jsoup.Jsoup;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.Html;
import utils.Link;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DefaultScraper implements Scraper {
    private static final Set<WebDriver> drivers = ConcurrentHashMap.newKeySet();
    private final ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(DefaultScraper::initDriver);
    private static final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();

    private static WebDriver initDriver() {
        var options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.openqa.selenium.remote.ProtocolHandshake").setLevel(Level.OFF);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        var driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        drivers.add(driver);
        return driver;
    }

    @Override
    public Html scrape(Link link) {
        WebDriver driver = driverThreadLocal.get();
        driver.get(link.toString());
        var url = new Link(driver.getCurrentUrl());
        if (!url.getWithoutProtocol().equals(link.getWithoutProtocol())) {
            LoggerUtils.debugLog.info(String.format("Redirect from %s to %s", link, url));
            LoggerUtils.consoleLog.info(String.format("Redirect from %s to %s", link, url));
        }
        var pageSource = driver.getPageSource();
        var html = new Html(pageSource, url);
        return hasRightLang(html) ? html : Html.emptyHtml();
    }

    @Override
    public void quit() {
        drivers.forEach(WebDriver::quit);
    }

    public Collection<String> getNewWords(Html html1, Html html2) {
        String htmlStr1 = Jsoup.parse(html1.toString()).text();
        String htmlStr2 = Jsoup.parse(html2.toString()).text();
        return diffMatchPatch.getNewWords(" " + htmlStr1 + " ", " " + htmlStr2 + " ");
    }

    private boolean hasRightLang(Html html) {
        var siteLangs = System.getProperty("site.langs");
        var htmlLang = html.getLang();
        if (htmlLang != null) {
            for (String siteLang : siteLangs.split(",")) {
                if (siteLang.contains(htmlLang) || htmlLang.contains(siteLang)) return true;
            }
        } else {
            return System.getProperty("ignore.html.without.lang").equals("false");
        }
        return false;
    }
}
