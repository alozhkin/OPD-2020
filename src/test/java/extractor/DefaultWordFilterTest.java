package extractor;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultWordFilterTest {

    @Test
    void filterTest() {
        Collection<String> wordsForFiltration = new HashSet<>();
        wordsForFiltration.add("ГаллОграмма..");
        wordsForFiltration.add("в");
        wordsForFiltration.add("Музее,,");
        wordsForFiltration.add("к");
        wordsForFiltration.add("11>");
        wordsForFiltration.add("сентября");
        wordsForFiltration.add("http://www.google.com");
        wordsForFiltration.add(",");
        wordsForFiltration.add("");
        Collection<String> expected = new HashSet<>();
        expected.add("галлограмма");
        expected.add("музее");
        expected.add("сентября");
        Collection<String> filteredSet = new DefaultWordFilter().filter(wordsForFiltration);
        assertEquals(expected, filteredSet);
    }
}