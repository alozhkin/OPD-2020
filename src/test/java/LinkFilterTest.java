import config.ConfigurationUtils;
import crawler.DefaultLinkFilter;
import crawler.LinkFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Link;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkFilterTest {
    private LinkFilter linkFilter;

    @BeforeEach
    void init() {
        linkFilter = new DefaultLinkFilter();
        ConfigurationUtils.configure();
    }

    @Test
    void shouldAcceptNormalLink() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/about")), "");
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksOnAnotherLanguage() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/fr")), "");
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksOnAnotherLanguageSegmentIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/fr/paris")), "");
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldAcceptLinksOnRightLanguage() {
        var langs = System.getProperty("site.langs").split(",");
        var filtered = linkFilter.filter(Set.of(new Link("example.com/" + langs[0] + "/")), "");
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtension() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.java")), "");
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtension() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.html")), "");
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtensionSegmentIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/path/index.java")), "");
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtensionSegmentIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/path/index.html")), "");
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtensionQueryIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.html?value=true")), "");
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldAcceptLinksWithRightFileExtensionSubdomainIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("sub.example.com/path/index.html")), "");
        assertEquals(1, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtensionQueryIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("example.com/index.java?value=true")), "");
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithWrongFileExtensionSubdomainIncluded() {
        var filtered = linkFilter.filter(Set.of(new Link("sub.example.com/path/index.java")), "");
        assertEquals(0, filtered.size());
    }

    @Test
    void shouldNotAcceptLinksWithUserInfo() {
        var filtered = linkFilter.filter(Set.of(new Link("http://mailto:beate.nowak@zwick-edelstahl.de/impressm")), "");
        assertEquals(0, filtered.size());
    }
}
