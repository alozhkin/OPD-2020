import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class TestCrawler {
    //List<String> urls;{try{urls=newArrayList<>(Files.readAllLines(Paths.get("src/test/resources/Url's Jsoup.org.txt")));}catch(IOException e){e.printStackTrace(); }}

    public static String rootURL = "https://jsoup.org/";
    public static String rootURL2 = "https://jsoup.org/download";

    public static void main(String[] args) {
        try {
            Document document = Jsoup.connect(rootURL2).get();
            List<Link> zzz = new DefaultCrawler().crawl(new Html(document.toString(), new Link(rootURL)));

            DefaultLinkFilter ff = new DefaultLinkFilter();

            for (Link each : zzz) System.out.println(each);

            System.out.println("||||||||||||||||||||||||||||||Desty_Fistek|||||||||||||||||||||||||||||||||||||||");

            ///Set<Link> ppp = ff.filter(zzz, rootURL);

            //for (Link each : ppp) System.out.println(each);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}