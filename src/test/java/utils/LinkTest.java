package utils;

import org.junit.jupiter.api.Test;

import java.net.IDN;

import static org.junit.jupiter.api.Assertions.*;

public class LinkTest {

    @Test
    void shouldThrowExceptionOnWrongURL() {
        var url = "wrong url [ ] & . oh so wrong";
        assertThrows(WrongFormedLinkException.class, () -> new Link(url));
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
        assertEquals("http://example:8080", new Link(url).getWithoutQueryUserInfoAndFragment());
    }

    @Test
    void shouldGetUrlWithoutFragmentAndQueryPortExcluded() {
        var url = "http://example/?name=you#content";
        assertEquals("http://example", new Link(url).getWithoutQueryUserInfoAndFragment());
    }

    @Test
    void shouldGetUrlWithoutFragmentAndQueryPathIncluded() {
        var url = "http://example/ttt?name=you#content";
        assertEquals("http://example/ttt", new Link(url).getWithoutQueryUserInfoAndFragment());
    }

    @Test
    void shouldWorkWithQueryWithoutEqualsSign() {
        var url = "https://jsoup.org/apidocs/index.html?org/jsoup/select/Elements.html";
        assertDoesNotThrow(new Link(url)::getParams);
    }

    @Test
    void shouldParseUmlaut() {
        var url = "www.erlebnisregion-schwäbischer-albtrauf.de";
        var idnUrl = IDN.toASCII(url);
        assertEquals(idnUrl, new Link(url).getHost());
    }

    @Test
    void shouldParseSpace() {
        var url = "www.bischer-albtrauf.de/grand tour";
        assertEquals("/grand tour", new Link(url).getPath());
    }

    @Test
    void shouldParseEncodedPath() {
        var url = "http://www.alce.at/schlacht-und-zerleges%C3%A4gen";
        assertEquals("/schlacht-und-zerlegesägen", new Link(url).getPath());
    }

    @Test
    void shouldFixWWW() {
        var url = "https://www.breitwiesenhaus.de/cms/wp-login.php?redirect_to=https://www.breitwiesenhaus.de/cms/wp-" +
                "admin/&reauth=1";
        assertEquals("breitwiesenhaus.de",new Link(url).fixWWW().getHost());
    }
}
