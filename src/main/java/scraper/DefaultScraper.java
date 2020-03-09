package scraper;

import org.jsoup.Jsoup;
import util.HTML;
import util.Link;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class DefaultScraper implements Scraper {

    private BlockingQueue<Link> linkQueue;
    private BlockingQueue<HTML> HTMLQueue;

    public DefaultScraper(BlockingQueue<Link> linkQueue, BlockingQueue<HTML> HTMLQueue) {
        this.linkQueue = linkQueue;
        this.HTMLQueue = HTMLQueue;
    }

    public void start() {
        while (true) {
            try {
                HTMLQueue.put(scrape(linkQueue.take()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private HTML scrape(Link link) {
        // TODO
        try {
            return new HTML(Jsoup.connect(link.toString()).get().toString(), link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
