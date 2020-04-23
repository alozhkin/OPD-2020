package link_filtration;

import config.ConfigurationUtils;
import database.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spider.*;
import utils.Link;

import java.util.Set;

public class LinkFiltration {
    @BeforeEach
    void configure() {
        ConfigurationUtils.configure();
    }

    @Test
    void getAllLinksFromSite() {
        var factory = new LinkFiltrationTestContextFactory();
        var spider = new Spider(factory, Database.createDummy());
        spider.scrapeDomains(Set.of(new Link("jsoup.org")));
        var context = (LinkFiltrationTestContext) factory.getContexts().get(0);
        var rejected = context.getAll();
        var accepted = context.getAccepted();
        for (Link link : accepted) {
            rejected.remove(link);
        }
        System.out.println("Accepted");
        for (Link link : accepted) {
            System.out.println(link);
        }
        System.out.println("-".repeat(80));
        System.out.println("Rejected");
        for (Link link : rejected) {
            System.out.println(link);
        }
    }

    @Test
    void getAllLinksFromPage() {
        var factory = new DefaultContextFactory();
        var context = factory.createContext();
        var html = context.scrape(new Link("jsoup.org"));
        var links = context.crawl(html);
        var accepted = context.filterLinks(links, html.getUrl());
        for (Link link : accepted) {
            links.remove(link);
        }
        System.out.println("Accepted");
        for (Link link : accepted) {
            System.out.println(link);
        }
        System.out.println("-".repeat(80));
        System.out.println("Rejected");
        for (Link link : links) {
            System.out.println(link);
        }
    }
}
