import crawler.Crawler;
import crawler.DefaultCrawler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Html;
import utils.Link;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrawlerTest {
    Crawler crawler;

    @BeforeEach
    void init() {
        crawler = new DefaultCrawler();
    }

    @Test
    void shouldNotFailOnEmptyHtml() throws IOException {
        Set<String> inSet = Set.of(
                "http://telefort.spb.ru/contacts.htm",
                "http://telefort.spb.ru/obj.pdf",
                "http://telefort.spb.ru/partners.htm",
                "http://telefort.spb.ru/index.html",
                "http://telefort.spb.ru/vacancy.htm",
                "http://telefort.spb.ru/documents.htm",
                "http://metrika.yandex.ru/stat/?id=6380740&from=informer",
                "https://pbx.telefort.spb.ru/"
        );
        Set<String> inSet2 = Set.of(
                "https://www.viennahouse.com/en.html",
                "https://www.viennahouse.com/en/all-hotels/list-of-hotels.html",
                "https://www.viennahouse.com/en/deals.html",
                "https://www.viennahouse.com/en/company/contact.html",
                "https://www.viennahouse.com/en/voucher.html",
                "https://www.viennahouse.com/de.html",
                "https://www.viennahouse.com/cz.html",
                "https://www.viennahouse.com/pl.html",
                "https://www.viennahouse.com/fr.html",
                "https://www.viennahouse.com/en/company/explore/who-we-are.html",
                "https://www.viennahouse.com/en/hotel-world.html",
                "https://www.viennahouse.com/en/company/explore/the-why.html",
                "https://www.viennahouse.com/en/company/explore/management-board.html",
                "https://www.viennahouse.com/en/company/investor-relations/vienna-house-group.html",
                "https://www.viennahouse.com/en/company/investor-relations/vienna-house-as-partner.html",
                "https://www.viennahouse.com/en/company/investor-relations/shareholder-structure.html",
                "https://www.viennahouse.com/en/company/investor-relations/sustainability.html",
                "https://www.viennahouse.com/en/company/press/press-team.html",
                "https://www.viennahouse.com/en/company/press/press-releases.html",
                "https://www.viennahouse.com/en/company/press/press-pictures/hotel-pictures-download.html",
                "https://www.viennahouse.com/en/company/benefits/best-rate-guarantee.html",
                "https://www.viennahouse.com/en/company/benefits/miles-more.html",
                "https://www.viennahouse.com/en/career/your-vienna-house.html",//*
                "https://www.viennahouse.com/en/career/inside/overview.html",
                "https://www.viennahouse.com/en/career/inside/values.html",
                "https://www.viennahouse.com/en/career/benefits.html",
                "https://www.viennahouse.com/en/career/develop.html",
                "https://www.viennahouse.com/en/career/awards.html",
                "https://www.viennahouse.com/en/career/job-offers.html",
                "https://www.viennahouse.com/en/development/project-development.html",
                "https://www.viennahouse.com/en/development/hotel-concepts.html",
                "https://www.viennahouse.com/en/development/why-partner.html",
                "https://www.viennahouse.com/en/development/our-ideal-property.html",
                "https://www.viennahouse.com/en/development/contracts-investments.html",
                "https://www.viennahouse.com/en/development/our-services.html",
                "https://www.viennahouse.com/en/development/our-services/strategy.html",
                "https://www.viennahouse.com/en/development/our-services/project-process.html",
                "https://www.viennahouse.com/en/development/our-services/interior-design.html",
                "https://www.viennahouse.com/en/development/our-services/technical-services.html",
                "https://www.viennahouse.com/en/development/preferred-locations.html",
                "https://www.viennahouse.com/en/development/new-projects.html",
                "https://www.viennahouse.com/en/development/contact.html",
                "https://www.viennahouse.com/en/kids/kids-home.html",
                "https://www.viennahouse.com/en/kids/johs-story.html",
                "https://www.viennahouse.com/en/kids/for-parents.html",
                "https://www.viennahouse.com/en/kids/just-for-kids.html",
                "https://www.viennahouse.com/en/kids/gamesnfun.html",
                "https://www.viennahouse.com/en/kids/johs-places2be.html",
                "https://www.viennahouse.com/en/meetings-events.html",
                "https://www.viennahouse.com/en/blog-stories/explorer-magazine.html",
                "https://www.viennahouse.com/en/newsletter.html",
                "https://www.viennahouse.com/en/terms.html",
                "https://www.viennahouse.com/en/all-hotels/your-request.html",
                "https://www.viennahouse.com/en/andels-cracow/whats-new.html",
                "https://www.viennahouse.com/en/mq-kronberg/the-hotel/overview.html",
                "https://www.viennahouse.com/en/company/explore/vienna-house.html",
                "https://www.viennahouse.com/en/company/explore/vienna-house-easy.html",
                "https://www.viennahouse.com/en/company/explore/vienna-townhouse.html",
                "https://www.viennahouse.com/en/company/explore/vienna-house-revo.html",
                "https://www.viennahouse.com/en/data-protection.html",
                "https://www.viennahouse.com/en/sitemap.html",
                "https://www.viennahouse.com/en/legal-notice.html",
                "https://www.viennahouse.com/en/deals/special-offers.html",

                "http://blog.viennahouse.com/en",
                "https://www.facebook.com/vienna.house.stories",
                "https://www.twitter.com/vh_stories",
                "https://www.instagram.com/vienna.house.stories",
                "https://www.youtube.com/user/VIHotels",
                "https://www.linkedin.com/company/vienna-international-hotelmanagement-ag",

                "https://www.viennahouse.com/#"

        );
        Set<Link> editedInSet = inSet.stream().map(Link::new).collect(Collectors.toSet());
        Html html = Html.fromFile(Path.of("src/test/resources/telefort.spb.ru.html"), new Link("http://telefort.spb.ru/"));
        assertEquals(editedInSet, crawler.crawl(html));

        editedInSet = inSet2.stream().map(Link::new).collect(Collectors.toSet());
        html = Html.fromFile(Path.of("src/test/resources/www.viennahouse.com.html"), new Link("https://www.viennahouse.com"));
        assertEquals(editedInSet, crawler.crawl(html));
    }
}