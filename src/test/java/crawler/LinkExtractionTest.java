//package crawler;
//
//import config.ConfigurationUtils;
//import main.Main;
//import org.jsoup.Jsoup;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import scraper.DefaultScraper;
//import utils.Html;
//import utils.Link;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.concurrent.ExecutionException;
//
//public class LinkExtractionTest {
//    DefaultLinkFilter filter = new DefaultLinkFilter();
//    DefaultCrawler crawler = new DefaultCrawler();
//    DefaultScraper scraper = new DefaultScraper();
//
//    @BeforeEach
//    void configure() {
//        ConfigurationUtils.configure();
//    }
//
//    @Test
//    void scrapePage() throws IOException {
//        filter.addDomain();
//        String link = "https://albrecht-steuerbuero.de";
//        Html html = scraper.scrape(new Link(link));
//        var links = crawler.crawl(html);
//        var filteredLinks = filter.filter(links, html.getUrl());
//        for (Link l : filteredLinks) {
//            System.out.println(l);
//        }
//        System.out.println("______------------________________________----------------_________________");
//        for (Link l : links) {
//            if (!filteredLinks.contains(l)) {
//                System.out.println(l);
//            }
//        }
//    }
//
//    @Test
//    void scrapeSite() throws IOException, ExecutionException, InterruptedException {
//        Link domain = new Link("algro-remscheid.de");
//        var res = Main.runWithoutWordsExtracting(domain);
//        var all = res[0];
//        var filtered = res[1];
//        for (Object l : filtered) {
//            System.out.println(l);
//        }
//        System.out.println("______------------________________________----------------_________________");
//        for (Object l : all) {
//            if (!filtered.contains(l)) {
//                System.out.println(l);
//            }
//        }
//    }
//
//    @Test
//    void foo() throws IOException {
//        System.out.println(Jsoup.parse(new URL(new Link("https://www.amber-ag.de").toString()), 1000));
//    }
//}