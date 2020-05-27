package extractor;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultWordFilterTest {

    @Test
    void filterTest() {
        Collection<String> wordSForFiltrationSet = new HashSet<>();
        wordSForFiltrationSet.add("ГолОграмма..");
        wordSForFiltrationSet.add("в");
        wordSForFiltrationSet.add("Музее,,");
        wordSForFiltrationSet.add("к");
        wordSForFiltrationSet.add("11>");
        wordSForFiltrationSet.add("http://www.google.com");
        wordSForFiltrationSet.add("сентября");
        wordSForFiltrationSet.add(",");
        Collection<String> expected = new HashSet<>();
        expected.add("голограмма");
        expected.add("музее");
        expected.add("сентября");
        Collection<String> filteredSet = new DefaultWordFilter().filter(wordSForFiltrationSet);
        assertEquals(expected, filteredSet);
    }

    @Test
    void wordsToLowerCaseTest() {
        Collection<String> wordSForFiltrationSet = new HashSet<>();
        wordSForFiltrationSet.add("Голограмма");
        wordSForFiltrationSet.add("муЗее");
        wordSForFiltrationSet.add("СентябрЯ");
        Collection<String> expected = new HashSet<>();
        expected.add("голограмма");
        expected.add("музее");
        expected.add("сентября");
        Collection<String> filteredSet = new DefaultWordFilter().filter(wordSForFiltrationSet);
        assertEquals(expected, filteredSet);
    }

    @Test
    void unnecessaryWordsFilterTest() {
        Collection<String> wordSForFiltrationSet = new HashSet<>();
        wordSForFiltrationSet.add("голограмма");
        wordSForFiltrationSet.add("в");
        wordSForFiltrationSet.add("музее");
        wordSForFiltrationSet.add("к");
        wordSForFiltrationSet.add("сентября");
        Collection<String> expected = new HashSet<>();
        expected.add("голограмма");
        expected.add("музее");
        expected.add("сентября");
        Collection<String> filteredSet = new DefaultWordFilter().filter(wordSForFiltrationSet);
        assertEquals(expected, filteredSet);
    }

    @Test
    void deleteBlankLinesTest() {
        Collection<String> wordSForFiltrationSet1 = new HashSet<>();
        wordSForFiltrationSet1.add("игорь");
        wordSForFiltrationSet1.add("");
        Collection<String> filteredSet1 = new DefaultWordFilter().filter(wordSForFiltrationSet1);
        assertEquals(1, filteredSet1.size());
        Collection<String> wordSForFiltrationSet2 = new HashSet<>();
        wordSForFiltrationSet2.add("игорь");
        Collection<String> filteredSet2 = new DefaultWordFilter().filter(wordSForFiltrationSet2);
        assertEquals(1, filteredSet2.size());
    }

    @Test
    void punctuationMarkFilterTest() {
        Collection<String> wordSForFiltrationSet = new HashSet<>();
        wordSForFiltrationSet.add("но-га...;.//,");
        wordSForFiltrationSet.add("///зе-бр-ы:...");
        Collection<String> expected = new HashSet<>();
        expected.add("но-га");
        expected.add("зе-бр-ы");
        Collection<String> newSet = new DefaultWordFilter().filter(wordSForFiltrationSet);
        assertEquals(expected, newSet);
    }

    @Test
    void linkFilteringTest() {
        Collection<String> wordsForFiltration = new HashSet<>();
        wordsForFiltration.add("нога");
        wordsForFiltration.add("https://www.google.com");
        wordsForFiltration.add("http://www.google.com");
        wordsForFiltration.add("зебры");
        wordsForFiltration.add("www.google.com");

        Collection<String> expected = new HashSet<>();
        expected.add("нога");
        expected.add("зебры");
        Collection<String> newSet = new DefaultWordFilter().filter(wordsForFiltration);
        assertEquals(expected, newSet);
    }

    @Test
    void numberFilteringTest() {
        Collection<String> wordsForFiltrationSet = new HashSet<>();

        wordsForFiltrationSet.add("голограмма");
        wordsForFiltrationSet.add("музее");
        wordsForFiltrationSet.add("11");
        wordsForFiltrationSet.add("сентября");

        Collection<String> expected = new HashSet<>();

        expected.add("голограмма");
        expected.add("музее");
        expected.add("сентября");

        Collection<String> newSet = new DefaultWordFilter().filter(wordsForFiltrationSet);

        assertEquals(expected, newSet);
    }
}