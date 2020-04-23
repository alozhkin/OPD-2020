package beta_test;

import config.ConfigurationUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spider.DefaultContextFactory;
import utils.Link;


public class WordExtraction {
    @BeforeAll
    static void configure() {
        ConfigurationUtils.configure();
    }

    @Test
    void extractPage() {
        var factory = new DefaultContextFactory();
        var context = factory.createContext();
        var html = context.scrape(new Link("jsoup.org"));
        var words = context.extract(html);
        var accepted = context.filterWords(words);
        for (String word : accepted) {
            words.remove(word);
        }
        System.out.println("Accepted");
        for (String word : accepted) {
            System.out.println(word);
        }
        System.out.println("-".repeat(80));
        System.out.println("Rejected");
        for (String word : words) {
            System.out.println(word);
        }
    }
}
