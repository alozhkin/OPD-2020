package scraper;

import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class DefaultScraper implements Scraper {

    private BlockingQueue<Link> linkQueue;
    private BlockingQueue<Html> HtmlQueue;

    public DefaultScraper(BlockingQueue<Link> linkQueue, BlockingQueue<Html> HtmlQueue) {
        this.linkQueue = linkQueue;
        this.HtmlQueue = HtmlQueue;
    }

    public void start() {
        while (true) {
            try {
                HtmlQueue.put(scrape(linkQueue.take()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Html scrape(Link link) {
        // TODO
        try {
            return new Html(Jsoup.connect(link.toString()).get().toString(), link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.selenium.dev/");
//        driver.findElement(By.id("search-by")).sendKeys("AAAAa");
        driver.findElement(By.id("logo")).click();
    }
}
