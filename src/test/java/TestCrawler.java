import crawler.DefaultCrawler;
import crawler.DefaultLinkFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class TestCrawler {
    public static String rootURL = "https://jsoup.org/";
    public static String rootURL2 = "https://jsoup.org/download";

    public static void main(String[] args) {
        try {
            Document document = Jsoup.connect(rootURL).get();
            List<Link> zzz = new DefaultCrawler().crawl(new Html(document.toString(), new Link(rootURL)));
           // for (Link temp : zzz) {System.out.println(temp);}
            DefaultLinkFilter ff = new DefaultLinkFilter();

            document = Jsoup.connect(rootURL2).get();
            zzz.addAll(new DefaultCrawler().crawl(new Html(document.toString(), new Link(rootURL2))));

            System.out.println("||||||||||||||||||||||||||||||Desty_Fistek|||||||||||||||||||||||||||||||||||||||");

            Set<Link> ppp = ff.filter(zzz, rootURL);

            //   for (Link each : ppp) System.out.println(each);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}