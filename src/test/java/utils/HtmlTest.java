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
}
