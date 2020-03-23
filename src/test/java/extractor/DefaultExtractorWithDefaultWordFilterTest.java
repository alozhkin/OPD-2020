package extractor;

import org.junit.jupiter.api.Test;
import util.HTML;
import util.Link;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultExtractorWithDefaultWordFilterTest {
    Path path = Paths.get("src/test/java/resources/html_for_test.txt");
    HTML h = new HTML(path);

    @Test
    void extractWithWordFilterTest() {
        Set<String> expected = Set.of("простенький", "be1ru", "html", "теста", "проверим", "вытащит", "отфильтрует",
                "список", "повторяющихся", "проверки", "совпадений", "слов", "ыыыыы", "непотребство", "вставить", "англ",
                "слова", "прикольно", "примеру", "gamburger", "немецком", "versuchs", "und", "lehranstalt", "für",
                "brauerei", "berlin", "vlb", "ev");
        Collection<String> set = new DefaultExtractor().extract(h);
        Collection<String> setWithFiltration = new DefaultWordFilter().filter(set);

        assertEquals(setWithFiltration, expected);
    }

}
