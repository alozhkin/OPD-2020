package extractor;

import org.junit.jupiter.api.Test;
import utils.Html;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultExtractorWithDefaultWordFilterTest {
    private Html h = Html.fromFile(Paths.get("src/test/resources/html_for_test.html"));
    public DefaultExtractorWithDefaultWordFilterTest() throws IOException {
    }

    @Test
    void extractWithWordFilterTest() {
        Set<String> expected = Set.of("juliana", "kommt", "paris", "diesem", "sommer", "universitätsstadt",
                "süden", "morgens", "klasse", "german", "noch", "kommen", "beste", "freundin");
        Collection<String> set = new DefaultExtractor().extract(h);
        Collection<String> setWithFiltration = new DefaultWordFilter().filter(set);
        assertEquals(new TreeSet<>(expected), new TreeSet<>(setWithFiltration));
    }
}
