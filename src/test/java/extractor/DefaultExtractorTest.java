package extractor;

import org.junit.jupiter.api.Test;
import utils.Html;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultExtractorTest {
    private Html h = Html.fromFile(Paths.get("src/test/resources/html_for_test.txt"));
    DefaultExtractorTest() throws IOException {
    }

    @Test
    void extractTest() {
        Set<String> expected = Set.of("Простенький", "be1.ru", "html,", "теста",
                "Проверим", "он", "вытащит", "и",
                "отфильтрует", "все", "это", "Список",
                "повторяющихся", "список", "для",
                "проверки", "совпадений", "слов", "ыыыыы", "Тут",
                "непотребство", "Надо", "бы", "еще",
                "вставить", "англ", "слова", "Прикольно,", "да?",
                "К", "примеру", "как", "gamburger", "или", "что", "то", "на", "немецком",
                "Versuchs-", "und", "Lehranstalt", "für", "Brauerei",
                "in", "Berlin", "(VLB)", "e.V.");
        Collection<String> set = new DefaultExtractor().extract(h);
        assertEquals(expected, set);
    }
}