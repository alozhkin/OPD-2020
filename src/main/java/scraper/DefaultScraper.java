package scraper;

import diff_match_patch.DiffMatchPatch;
import org.jsoup.Jsoup;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
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
        return new Html(driver.getPageSource(), link);
    }

    public Collection<String> getNewWords(Html html1, Html html2) {
        var a = Jsoup.parse(html1.toString()).text();
        var b = Jsoup.parse(html2.toString()).text();
        return diffMatchPatch.getNewWords(" " + a + " ", " " + b + " ");
    }

    public void quit() {
        drivers.forEach(WebDriver::quit);
    }
}
