package crawler;

import config.ConfigurationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Link;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkFilterTest {
    private DefaultLinkFilter linkFilter;

    @BeforeEach
    void init() {
        linkFilter = new DefaultLinkFilter();
        ConfigurationUtils.configure();
    }

    @Test
    void shouldAcceptNormalLink() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/about")), new Link("example.com"));
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksOnAnotherLanguage() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/fr")), new Link("example.com"));
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksOnAnotherLanguageSegmentIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/fr/paris")), new Link("example.com"));
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldAcceptLinksOnRightLanguage() {
        var langs = System.getProperty("site.langs").split(",");
        var filtered = linkFilter.filter(Set.of(new Link("example.com/" + langs[0] + "/")), new Link("example.com"));
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtension() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.java")), new Link("example.com"));
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtension() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.html")), new Link("example.com"));
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtensionSegmentIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/path/index.java")), new Link("example.com"));
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtensionSegmentIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/path/index.html")), new Link("example.com"));
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtensionQueryIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.htm?value=true")), new Link("example.com"));
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtensionSubdomainIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("sub.example.com/path/index.html")), new Link("example.com"));
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtensionQueryIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.java?value=1")), new Link("example.com"));
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtensionSubdomainIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("sub.example.com/path/index.java")), new Link("example.com"));
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithUserInfo() {
        var filtered = linkFilter.filter(
                Set.of(new Link("http://mailto:beate.nowak@zwick-edelstahl.de/impressum")), new Link("example.com")
        );
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotBeConfusedWithDotsInPath() {
        var filtered = linkFilter.filter(
                Set.of(new Link("http://www.jsoup.org/packages/jsoup-1.13.1.jar")), new Link("jsoup.org")
        );
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldIgnoreLinksWithFragmentDifferent() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("https://github.com/features#issue"),
                        new Link("https://github.com/features#hosting")
                ),
                new Link("github.com")
        );
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotConsiderLinksWithQueryDifferent() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("https://github.com/features"),
                        new Link("https://github.com/features?value=1")
                ),
                new Link("github.com")
        );
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotConsiderLinksWithQueryDifferent2() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("https://github.com/features?value=2"),
                        new Link("https://github.com/features?value=1")
                ),
                new Link("github.com")
        );
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldConsiderLinksWithContentQueryDifferent() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("https://www.alfa-tools.de/very_useful.php?content=datenschutz"),
                        new Link("https://www.alfa-tools.de/very_useful.php?content=montage")
                ),
                new Link("alfa-tools.de")
        );
        assertEquals(2, filtered.size());
    }

    @Test
    void shouldConsiderLinksWithContentQueryDifferentSeveralParams() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("https://www.alfa-tools.de/very_useful.php?value=true&content=datenschutz"),
                        new Link("https://www.alfa-tools.de/very_useful.php?value=false&content=montage")
                ),
                new Link("alfa-tools.de")
        );
        assertEquals(2, filtered.size());
    }

    @Test
    void shouldNotConsiderLinksWithDifferentProtocolsDifferent() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("https://www.les-graveurs.de/path"),
                        new Link("http://www.les-graveurs.de/path")
                ),
                new Link("les-graveurs.de")
        );
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldUnderstandUmlautInUrl() {
        var filtered = linkFilter.filter(
                Set.of(new Link("https://www.matratzen.de/Gewährleistung")), new Link("www.matratzen.de")
        );
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotConsiderLinksWithWWWDifferent() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("www.les-graveurs.de/path"),
                        new Link("les-graveurs.de/path")
                ),
                new Link("www.les-graveurs.de")
        );
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldAddDefaultPagesToOccurred() {
        linkFilter.addDomain();
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("example.com"),
                        new Link("example.com/index.php"),
                        new Link("example.com/index.html"),
                        new Link("example.com/index.aspx")
                ),
                new Link("example.com")
        );
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldIgnoreCertainPages() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("example.com/agb"),
                        new Link("example.com/news"),
                        new Link("example.com/blog")
                ),
                new Link("example.com")
        );
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldIgnoreCertainSubdomains() {
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("ordershop.example.com"),
                        new Link("shop.example.com")
                ),
                new Link("example.com")
        );
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldIgnoreLinksAfterRedirect() {
        linkFilter.filter(Set.of(), new Link("example.com/redirected"), new Link("example.com/redirect"));
        var filtered = linkFilter.filter(
                Set.of(
                        new Link("example.com/redirect")
                ),
                new Link("example.com")
        );
        assertEquals(0, filtered.size());
    }
}
