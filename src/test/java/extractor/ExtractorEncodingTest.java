package extractor;

import org.junit.jupiter.api.Test;
import utils.Html;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtractorEncodingTest {

    @Test
    public void extractorShouldSupportEncodingMeta1() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(Html.fromFile(Paths.get("src/test/resources/encodings/Windows1251_meta1.html")));
        assertTrue(res.contains("тест"));
    }

    @Test
    public void extractorShouldSupportEncodingMeta2() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(Html.fromFile(Paths.get("src/test/resources/encodings/Windows1251_meta2.html")));
        assertTrue(res.contains("тест"));
    }

    @Test
    public void extractorShouldSupportUTF8EncodingByDefault() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(Html.fromFile(Paths.get("src/test/resources/encodings/UTF8_without_meta.html")));
        assertTrue(res.contains("тест"));
    }

    @Test
    public void extractorShouldSupportUTF8Encoding() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(Html.fromFile(Paths.get("src/test/resources/encodings/UTF8.html")));
        assertTrue(res.contains("тест"));
    }
}
