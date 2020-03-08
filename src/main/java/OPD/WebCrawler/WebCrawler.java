package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

    public static void crawl(@NotNull Document document, String rootURL) {
        Elements linksOnPage = document.select("a[href]");
        for (Element page : linksOnPage) {
            String actualURL = page.attr("abs:href");
            if (CrawlersFilter.filter(actualURL, rootURL)) {
                System.out.println(actualURL);//Сбда встроить вывод ссылки в лист
            }
        }
    }
}

