package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TestCrawler {//Класс исключительно для тестировки WebCrawler
    public static String rootURL = "https://jsoup.org/";
    public static String rootURL2 = "https://jsoup.org/download";

    public static void main(@NotNull String[] args) {
        try {
            Document document = Jsoup.connect(rootURL).get();
            Links list = new Links();
            list.addAll(WebCrawler.crawl(document, rootURL, list).get());
            list.printALL();
            System.out.println("*********************Переход по ссылке****************************");

            document = Jsoup.connect(rootURL2).get();
            list.addAll(WebCrawler.crawl(document, rootURL, list).get());
            list.printALL();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
