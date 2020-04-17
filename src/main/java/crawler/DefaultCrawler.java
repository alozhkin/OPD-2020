package crawler;

import main.Main;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Html;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultCrawler implements Crawler {
    @Override
    public Collection<Link> crawl(@NotNull Html html) {
        Set<Link> list = new HashSet<>();
        Document doc = Jsoup.parse(html.toString(), html.getUrl().toString());
        Elements linksOnPage = doc.select("a[href]");

        for (Element page : linksOnPage) {
            Link url = new Link(page.attr("abs:href"));
            if (!url.toString().equals("") && url != html.getUrl()) {
                list.add(url);
            }
        }
        Main.debugLog.debug("Crawling task completed");
        return list;
    }
}
