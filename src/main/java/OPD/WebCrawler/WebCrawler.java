package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {

    public static Links crawl(@NotNull Document document, String rootURL, Links list) {
        Elements linksOnPage = document.select("a[href]");
        for (Element page : linksOnPage) {
            String actualURL = page.attr("abs:href");
            if (CrawlersFilter.filter(actualURL, rootURL, list)) {
                list.add(actualURL);
                //System.out.println(actualURL);//Сюда встроить вывод ссылки в лист
            }
        }
        return list;
    }
}

