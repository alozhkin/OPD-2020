package crawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.List;

public class DefaultCrawler implements Crawler {

    @Override
    public List<Link> crawl(@NotNull Html html) {
        List<Link> list = new ArrayList<>();

        Elements linksOnPage = Jsoup
                .parse(html.toString(), html.getUrl().toString())
                .select("a[href]");

        for (Element page : linksOnPage) {
            Link url = new Link(page.attr("abs:href"));
            if (!url.toString().equals(""))

                list.add(url);

        }
        return list;
    }
}
