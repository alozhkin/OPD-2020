package extractor;

import org.junit.jupiter.api.Test;
import utils.Html;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultExtractorTest {
    @Test
    void extractTest() throws IOException {
        var html = Html.fromFile(Paths.get("src/test/resources/html_for_test.html"));
        Set<String> expected = Set.of("&^4$", ",German,", "14", "Freundin", "In", "Juliana", "Klasse", "Morgens",
                "Paris", "Sie", "Sommer", "Süden", "Universitätsstadt", "aus", "be1.ru", "beste", "diesem", "kommen",
                "kommt", "noch", "r555f", "um");
        Collection<String> set = new DefaultExtractor().extract(html);
        assertEquals(new TreeSet<>(expected), new TreeSet<>(set));
    }
}