package scraper;

import diff_match_patch.DiffMatchPatch;
import main.Main;
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
import java.util.logging.Level;

public class DefaultScraper implements Scraper {
    private static Set<WebDriver> drivers = ConcurrentHashMap.newKeySet();
    private ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(DefaultScraper::initDriver);
    private static final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();

    private static WebDriver initDriver() {
        var options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        var driver = new ChromeDriver(options);
        drivers.add(driver);
        return driver;
    }

    @Override
    public Html scrape(Link link) {
        WebDriver driver = driverThreadLocal.get();
        driver.get(link.toString());
        var url = new Link(driver.getCurrentUrl());
        if (!url.getWithoutProtocol().equals(link.getWithoutProtocol())) {
            Main.debugLog.info(String.format("Redirect from %s to %s", link, url));
            Main.consoleLog.info(String.format("Redirect from %s to %s", link, url));
        }
        var html = new Html(driver.getPageSource(), url);
        return hasRightLang(html) ? html : Html.emptyHtml();
    }

    public Collection<String> getNewWords(Html html1, Html html2) {
        var a = Jsoup.parse(html1.toString()).text();
        var b = Jsoup.parse(html2.toString()).text();
        return diffMatchPatch.getNewWords(" " + a + " ", " " + b + " ");
    }

    private boolean hasRightLang(Html html) {
        var siteLangs = System.getProperty("site.langs");
        var htmlLang = html.getLang();
        if (htmlLang != null) {
            for (String siteLang : siteLangs.split(",")) {
                if (siteLang.contains(htmlLang) || htmlLang.contains(siteLang)) return true;
            }
        }
        return false;
    }

    public void quit() {
        drivers.forEach(WebDriver::quit);
    }
}
