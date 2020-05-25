package extractor;

import config.ConfigurationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for word filtration
 */
public class DefaultWordFilter implements WordFilter {

    private static final Collection<String> filteredWords = getFilterWords();

    /**
     * Method responsible for filtration. Filters words in list_of_words_for_filtration, digits, punctuation marks,
     * transorms all words to lower case.
     * @param words words to be filtered
     * @return filtered words
     */
    @Override
    public Collection<String> filter(Collection<String> words) {
        removeLinks(words);
        Collection<String> newSet = wordsToLowerCase(punctuationMarkFilter(words));
        deleteBlankLines(newSet);
        removeNumbers(newSet);
        unnecessaryWordsFilter(newSet);
        return newSet;
    }

    // Фильтр ненужных слов
    private void unnecessaryWordsFilter(Collection<String> words) {
        words.removeAll(filteredWords);
    }

    //Удаление пустого элемента
    private void deleteBlankLines(Collection<String> words) {
        words.removeIf(String::isEmpty);
    }

    private Collection<String> wordsToLowerCase(Collection<String> words) {
        return words.stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    //Фильтр знаков препинания
    private Collection<String> punctuationMarkFilter(Collection<String> words) {
        Collection<String> newSet = new HashSet<>();

        for (String setObj : words) {
            newSet.add(removingPunctuation(setObj));
        }
        return newSet;
    }

    //Метод удаления ссылок из коллекции слов
    private void removeLinks(Collection<String> words) {
        words.removeIf(x -> x.matches("^(www|http:|https:)+[^\\s\"]+[\\w]"));
    }

    //Метод удаления слов-чисел
    private void removeNumbers(Collection<String> words) {
        words.removeIf(x -> x.matches("^\\d+$"));
    }

    // Метод удаления знаков препинания из строки
    private String removingPunctuation(String s) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }

        return sb.toString();
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
}