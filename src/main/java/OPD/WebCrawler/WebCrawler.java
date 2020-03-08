package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

    public static Links crawl(@NotNull Document document, String rootURL, Links list) {//Сам Crawler: достает все URL from HTML, отпревляет в Filter и возвращает полный отфильтрованный список URL
        Elements linksOnPage = document.select("a[href]");
        for (Element page : linksOnPage) {
            String actualURL = page.attr("abs:href");
            if (CrawlersFilter.filter(actualURL, rootURL, list))
                list.add(actualURL);
            //System.out.println(actualURL);
        }
        return list;
    }
}

