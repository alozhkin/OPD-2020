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
import java.util.*;

import java.util.ArrayList;
import java.util.List;

public class TestCrawler {
    //List<String> urls;{try{urls=newArrayList<>(Files.readAllLines(Paths.get("src/test/resources/Url's Jsoup.org.txt")));}catch(IOException e){e.printStackTrace(); }}

    public static String rootURL = "https://jsoup.org/";
    public static String rootURL2 = "https://jsoup.org/download";

    public static void main(String[] args) {
        try {
            List<Link> zzz = new DefaultCrawler().crawl(new Html(Jsoup.connect(rootURL).get().toString(), new Link(rootURL)));

            DefaultLinkFilter ff = new DefaultLinkFilter();

            zzz.addAll(new DefaultCrawler().crawl(new Html(Jsoup.connect(rootURL2).get().toString(), new Link(rootURL2))));

            Set<Link> ppp = ff.filter(zzz, rootURL);

            for (Link each : ppp) System.out.println(each);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}