package extractor;

import config.ConfigurationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultWordFilterTest {
    private static DefaultWordFilter filter;

    @BeforeEach
    void init() {
        filter  = new DefaultWordFilter();
    }

    @Test
    void shouldFilterWordsWithWrongSymbolsInside() {
        var words = Set.of("rrr%rrr", "roa9d", "http://example.com");
        assertTrue(filter.filter(words).isEmpty());
    }

    @Test
    void shouldFilterEmptyStrings() {
        var words = Set.of("");
        assertTrue(filter.filter(words).isEmpty());
    }

    @Test
    void shouldUnderstandUmlaut() {
        var words = Set.of("äöüÄÖÜß");
        assertEquals(1, filter.filter(words).size());
    }

    @Test
    void shouldIgnoreWords() {
        var filteredWords = new ArrayList<String>();
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/english_words.txt", filteredWords, DefaultWordFilter.class
        );
        assertTrue(filter.filter(filteredWords).isEmpty());
    }
}