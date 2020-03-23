package scraper;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.Html;
import utils.Link;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultScraper implements Scraper {
    private static Set<WebDriver> drivers = ConcurrentHashMap.newKeySet();
    private ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(DefaultScraper::initDriver);

    private static WebDriver initDriver() {
        var options = new ChromeOptions();
        options.addArguments("--headless");
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

    public void quit() {
        drivers.forEach(WebDriver::quit);
    }
}
