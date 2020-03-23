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
    void shouldRemoveTrailingSlash() {
        var expected = "http://test.com";
        var slashUrl = "http://test.com/";
        assertEquals(expected, new Link(slashUrl).toString());
    }

    @Test
    void shouldParseQueryString() {
        var url = "http://test.com/?value=true";
        assertEquals(url, new Link(url).toString());
    }

    @Test
    void shouldParseAnchor() {
        var url = "http://test.com#content";
        assertEquals(url, new Link(url).toString());
    }

    @Test
    void shouldParseBrackets() {
        var url = "http://test.com/(java.lang.String)/Jsoup.html";
        assertEquals(url, new Link(url).toString());
    }

    @Test
    void shouldAddProtocol() {
        var expected = "http://test.com";
        var urlWithoutProtocol = "test.com";
        assertEquals(expected, new Link(urlWithoutProtocol).toString());
    }
}
