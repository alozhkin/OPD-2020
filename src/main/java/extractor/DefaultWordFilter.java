package extractor;

import config.ConfigurationUtils;
import logger.LoggerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Character.isDigit;

public class DefaultWordFilter implements WordFilter {

    private final Collection<String> filteredWords = getFilterWords();

    @Override
    public Collection<String> filter(Collection<String> words) {
        LoggerUtils.debugLog.debug("Filtration task started");
        Collection<String> newSet = wordsToLowerCase(punctuationMarkFilter(words));
        deleteBlankLines(newSet);
        unnecessaryWordsFilter(newSet);
        filterNumbers(newSet);
        LoggerUtils.debugLog.debug("Filtration task completed");
        return newSet;
    }

    // Фильтр ненужных слов
    public void unnecessaryWordsFilter(Collection<String> setOfWords) {
        setOfWords.removeAll(filteredWords);
    }

    //Удаление пустого элемента
    public void deleteBlankLines(Collection<String> setOfWords) {
        setOfWords.removeIf(String::isEmpty);
    }

    public Collection<String> wordsToLowerCase(Collection<String> setOfWords) {
        return setOfWords.stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    //фильтр слов-чисел
    public Collection<String> filterNumbers(Collection<String> words) {
        words.removeIf(x->x.matches("^\\d+$"));
        return words;
    }

    //Фильтр знаков препинания
    public Collection<String> punctuationMarkFilter(Collection<String> setOfWords) {
        Collection<String> newSet = new HashSet<>();

        for (String setObj : setOfWords) {
            newSet.add(removingPunctuation(setObj));
        }
        return newSet;
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

    private Collection<String> getFilterWords() {
        List<String> filteredWords = new ArrayList<>();
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/english_words.txt", filteredWords, getClass()
        );
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/russian_words.txt", filteredWords, getClass()
        );
        ConfigurationUtils.parseResourceToCollection(
                "list_of_words_for_filtration/german_words.txt", filteredWords, getClass()
        );
        return filteredWords;
    }
}