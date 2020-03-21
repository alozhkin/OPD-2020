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
        Document doc = Jsoup
                .parse(html.toString(), html.getUrl().toString());
        Elements linksOnPage = doc.select("a[href]");

        for (Element page : linksOnPage) {
            Link url = new Link(page.attr("abs:href"));
            if (!url.toString().equals("")) {
                String zzz = page.text();
                if (zzz.contains(".")) {
                    zzz = zzz.substring(zzz.lastIndexOf("."));
                    System.out.println(zzz);
                    //if (zzz) {
                    //    list.add(url);
                    //}
                } else list.add(url);
            }
        }
        return list;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
