import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

class WebCrawler {

    public static void addLinks(String link) {
       links.add(link);
    }

    public HashSet<String> getLinks() {
        return links;
    }

    public static void crawl(Document document, String rootURL) {

        Elements linksOnPage = document.select("a[href]");
        for (Element page : linksOnPage) {
            String actualURL = page.attr("abs:href");
            CrawlersFilter.filter(actualURL, rootURL);
        }


    }
}

