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

        Elements media = doc.select("[src]");
        Elements linksOnPage = doc.select("a[href]");
        for (Element zz : media) {
                System.out.println(zz.normalName());

        }

        for (Element page : linksOnPage) {
            Link url = new Link(page.attr("abs:href"));
            if (!url.toString().equals(""))

                list.add(url);

        }
        return list;
    }
    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
