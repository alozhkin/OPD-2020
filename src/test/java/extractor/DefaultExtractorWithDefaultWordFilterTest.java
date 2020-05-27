package extractor;

import org.junit.jupiter.api.Test;
import utils.Html;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultExtractorWithDefaultWordFilterTest {
    private Html h = Html.fromFile(Paths.get("src/test/resources/html_for_test.html"));
    public DefaultExtractorWithDefaultWordFilterTest() throws IOException {
    }

    @Test
    void extractWithWordFilterTest() {
        Set<String> expected = Set.of("простенький", "be1.ru", "html", "теста", "проверим", "вытащит", "отфильтрует",
                "список", "повторяющихся", "проверки", "совпадений", "слов", "ыыыыы", "непотребство", "вставить", "англ",
                "слова", "прикольно", "примеру", "gamburger", "немецком", "versuchs", "lehranstalt",
                "brauerei", "berlin", "vlb", "e.v");
        Collection<String> set = new DefaultExtractor().extract(h);
        Collection<String> setWithFiltration = new DefaultWordFilter().filter(set);
        assertEquals(setWithFiltration, expected);
    }
}
