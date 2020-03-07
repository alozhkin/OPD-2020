import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;

class WebCrawler {
    public static String rootURL = "https://4pda.ru/";
    private HashSet<String> links;

    public WebCrawler() {
        links = new HashSet<>();
    }

    public static void main(@NotNull String[] args) {
        crawl();
    }

    public static void crawl() {
        try {
            Document document = Jsoup.connect(rootURL).get();
            new WebCrawler().getPageLinks(document);
        } catch (IOException e) {
            System.err.println("For '" + rootURL + "': " + e.getMessage());
        }
    }

    public HashSet<String> getURLs() {
        return links;
    }

    public void getPageLinks(@NotNull Document document) {
        Elements linksOnPage = document.select("a[href]");
        for (Element page : linksOnPage) {
            String actualURL = page.attr("abs:href");
            if (actualURL.contains(rootURL))
                links.add(actualURL);
            //  if (links.add(actualURL))
            //      System.out.println(actualURL);
        }
        System.out.print(getURLs());
    }
}


