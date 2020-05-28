package extractor;

import config.ConfigurationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for word filtration
 */
public class DefaultWordFilter implements WordFilter {

    private static final Collection<String> filteredWords = getFilterWords();

    /**
     * Method responsible for filtration.
     * transorms all words to lower case.
     * @param words words to be filtered
     * @return filtered words
     */
    @Override
    public Collection<String> filter(Collection<String> words) {
        return words.stream().map(this::transform).filter(this::check).collect(Collectors.toSet());
    }

    private static Collection<String> getFilterWords() {
        List<String> filteredWords = new ArrayList<>();
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/english_words.txt", filteredWords, DefaultWordFilter.class
        );
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/russian_words.txt", filteredWords, DefaultWordFilter.class
        );
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/german_words.txt", filteredWords, DefaultWordFilter.class
        );
        return filteredWords;
    }

    private String transform(String str) {
        return str.replaceAll("^[^a-zA-ZäöüÄÖÜß]*|[^a-zA-ZäöüÄÖÜß]*$", "").toLowerCase();
    }

    private boolean check(String str) {
        return str.matches("[a-zA-ZäöüÄÖÜß\\-]+") && !filteredWords.contains(str);
    }
}