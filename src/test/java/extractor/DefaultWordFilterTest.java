package extractor;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class DefaultWordFilterTest {

    @Test
    void filterTest() throws IOException {
        Collection<String> wordSForFiltrationSet = new HashSet<>();
        wordSForFiltrationSet.add("ГаллОграмма..");
        wordSForFiltrationSet.add("в");
        wordSForFiltrationSet.add("Музее,,");
        wordSForFiltrationSet.add("к");
        wordSForFiltrationSet.add("11>");
        wordSForFiltrationSet.add("сентября");
        wordSForFiltrationSet.add(",");

        Collection<String> expected = new HashSet<>();
        expected.add("галлограмма");
        expected.add("музее");
        expected.add("11");
        expected.add("сентября");

        Collection<String> filteredSet = new DefaultWordFilter().filter(wordSForFiltrationSet);
        assertTrue(expected.containsAll(filteredSet));
        assertEquals(expected, filteredSet);
    }

    @Test
    void unnecessaryWordsFilterTest() throws IOException {
        Collection<String> wordSForFiltrationSet = new HashSet<>();
        wordSForFiltrationSet.add("Галлограмма");
        wordSForFiltrationSet.add("в");
        wordSForFiltrationSet.add("Музее");
        wordSForFiltrationSet.add("к");
        wordSForFiltrationSet.add("11");
        wordSForFiltrationSet.add("сентября");

        Collection<String> expected = new HashSet<>();
        expected.add("Галлограмма");
        expected.add("Музее");
        expected.add("11");
        expected.add("сентября");

        DefaultWordFilter.unnecessaryWordsFilter(wordSForFiltrationSet);
        assertEquals(wordSForFiltrationSet, expected);
    }

    @Test
    void deleteBlankLinesTest() {
        Collection<String> wordSForFiltrationSet1 = new HashSet<>();
        wordSForFiltrationSet1.add("Игорь");
        wordSForFiltrationSet1.add("");
        DefaultWordFilter.deleteBlankLines(wordSForFiltrationSet1);
        assertEquals(wordSForFiltrationSet1.size(), 1);

        Collection<String> wordSForFiltrationSet2 = new HashSet<>();
        wordSForFiltrationSet2.add("Игорь");
        DefaultWordFilter.deleteBlankLines(wordSForFiltrationSet1);
        assertEquals(wordSForFiltrationSet2.size(), 1);

        assertFalse(wordSForFiltrationSet2.size() == 0);
    }

    @Test
    void punctuationMarkFilterTest() {

        Collection<String> wordSForFiltrationSet = new HashSet<>();

        wordSForFiltrationSet.add("нога,");
        wordSForFiltrationSet.add("404..");
        wordSForFiltrationSet.add("Зебры:");
        wordSForFiltrationSet.add(".");


        Collection<String> expected = new HashSet<>();
        expected.add("нога");
        expected.add("404");
        expected.add("Зебры");
        expected.add("");

        Collection<String> newSet = DefaultWordFilter.punctuationMarkFilter(wordSForFiltrationSet);


        assertEquals(newSet, expected);


    }
}