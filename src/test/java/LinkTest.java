import org.junit.jupiter.api.Test;
import utils.Link;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkTest {

    @Test
    void shouldNotThrowExceptionOnWrongURL() {
        var url = "wrong url%$@&(.[] very wrong";
        assertDoesNotThrow(() -> new Link(url));
    }

    @Test
    void shouldConsiderWrongURLLikeEmptyString() {
        var url = "wrong url [ ] & . oh so wrong";
        assertEquals("", new Link(url).toString());
    }

    @Test
    void shouldAcceptEmptyString() {
        var url = "";
        assertEquals("", new Link(url).toString());
    }

    @Test
    void shouldRemoveTrailingSlash() {
        var expected = "http://test.com";
        var slashUrl = "http://test.com/";
        assertEquals(expected, new Link(slashUrl).toString());
    }

    @Test
    void shouldParseQueryString() {
        var url = "http://test.com/?value=true";
        assertEquals("value=true", new Link(url).getQuery());
    }

    @Test
    void shouldParseAnchor() {
        var url = "http://test.com#content";
        assertEquals("content", new Link(url).getFragment());
    }

    @Test
    void shouldParseBrackets() {
        var url = "http://test.com/(java.lang.String)/Jsoup.html";
        assertEquals(url, new Link(url).toString());
    }

    @Test
    void shouldAddProtocol() {
        var urlWithoutProtocol = "test.com";
        assertEquals("http://test.com", new Link(urlWithoutProtocol).toString());
    }

    @Test
    void shouldGetUrlWithoutFragmentAndQuery() {
        var url = "http://example:8080/?name=you#content";
        assertEquals("http://example:8080/", new Link(url).getWithoutQueryAndFragment());
    }

    @Test
    void shouldGetUrlWithoutFragmentAndQueryPortExcluded() {
        var url = "http://example/?name=you#content";
        assertEquals("http://example/", new Link(url).getWithoutQueryAndFragment());
    }

    @Test
    void shouldGetUrlWithoutFragmentAndQueryPathIncluded() {
        var url = "http://example/ttt?name=you#content";
        assertEquals("http://example/ttt", new Link(url).getWithoutQueryAndFragment());
    }
}
