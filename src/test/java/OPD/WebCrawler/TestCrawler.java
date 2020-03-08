package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TestCrawler {
    public static String rootURL = "https://4pda.ru/";

    public static void main(@NotNull String[] args) {
        try {
            Document document = Jsoup.connect(rootURL).get();
            WebCrawler.crawl(document, rootURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
