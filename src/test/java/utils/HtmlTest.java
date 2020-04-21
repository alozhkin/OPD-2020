package utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlTest {

    @Test
    void shouldReadLangAttr() throws IOException {
        var html = Html.fromFile(Paths.get("src/test/resources/scraper_res/wrong_language.html"));
        var lang = html.getLang();
        assertEquals("it", lang);
    }

    @Test
    void shouldReadLangAttrWithSeveralLangs() throws IOException {
        var html = Html.fromFile(Paths.get("src/test/resources/scraper_res/several_langs.html"));
        var lang = html.getLang();
        assertEquals("de-DE", lang);
    }

    @Test
    void shouldReadLangAttrFromMetaTag() throws IOException {
        var html = Html.fromFile(Paths.get("src/test/resources/scraper_res/meta_tag_lang.html"));
        var lang = html.getLang();
        assertEquals("de", lang);
    }

    @Test
    void shouldReadLangAttrFromMetaTag2() throws IOException {
        var html = Html.fromFile(Paths.get("src/test/resources/scraper_res/meta_tag_lang2.html"));
        var lang = html.getLang();
        assertEquals("de", lang);
    }

    @Test
    void shouldReadLangAttrFromMetaTag3() throws IOException {
        var html = Html.fromFile(Paths.get("src/test/resources/scraper_res/meta_tag_lang3.html"));
        var lang = html.getLang();
        assertEquals("de", lang);
    }
}
