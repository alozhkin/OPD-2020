import extractor.DefaultExtractor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ExtractorEncodingTest {

    @Test
    public void extractorShouldSupportEncodingMeta1() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(new File("src/test/resources/encodings/Windows1251_meta1.html"));
        assertTrue(res.contains("тест"));
    }

    @Test
    public void extractorShouldSupportEncodingMeta2() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(new File("src/test/resources/encodings/Windows1251_meta2.html"));
        assertTrue(res.contains("тест"));
    }

    @Test
    public void extractorShouldSupportUTF8EncodingByDefault() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(new File("src/test/resources/encodings/UTF8_without_meta.html"));
        assertTrue(res.contains("тест"));
    }

    @Test
    public void extractorShouldSupportUTF8Encoding() throws IOException {
        var extractor = new DefaultExtractor();
        var res = extractor.extract(new File("src/test/resources/encodings/UTF8.html"));
        assertTrue(res.contains("тест"));
    }
}
