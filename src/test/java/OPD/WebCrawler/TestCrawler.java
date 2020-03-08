package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TestCrawler {
    public static String rootURL = "https://lms.spbstu.ru/login/index.php";

    public static void main(@NotNull String[] args) {
        try {
            Document document = Jsoup.connect(rootURL).get();
            Links list = new Links();
            list.addAll(WebCrawler.crawl(document, rootURL, list).get());
            list.printALL();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
