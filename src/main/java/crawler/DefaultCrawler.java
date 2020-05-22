package crawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Html;
import utils.Link;
import utils.WrongFormedLinkException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class responsible for extracting links from html
 * Ignores not valid urls
 */
public class DefaultCrawler implements Crawler {
    @Override
    public Collection<Link> crawl(@NotNull Html html) {
        Set<Link> list = new HashSet<>();
        Document doc = Jsoup.parse(html.toString(), html.getUrl().toString());
        Elements linksOnPage = doc.select("a[href]");

        for (Element page : linksOnPage) {
            try {
                var attr = page.attr("abs:href");
                if (attr.equals("")) continue;
                Link url = new Link(attr);
                if (url != html.getUrl()) {
                    list.add(url);
                }
            } catch (WrongFormedLinkException ignored) {}
        }
        return list;
    }
}
