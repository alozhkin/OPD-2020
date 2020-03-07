import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashSet;

public class TestCrawler {

    public HashSet<String> links;//вынести во внешний класс

    public TestCrawler() {
        links = new HashSet<>();
    }

    public static String rootURL = "https://careerforums.ru/fresh-spb";

    public static void main(@NotNull String[] args) {
        try {
            Document document = Jsoup.connect(rootURL).get();
            WebCrawler.crawl(document, rootURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
