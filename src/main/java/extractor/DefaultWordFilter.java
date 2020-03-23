package extractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultWordFilter implements WordFilter {
    @Override
    public Collection<String> filter(Collection<String> words) {
        Collection<String> newSet = wordsToLowerCase(punctuationMarkFilter(words));
        deleteBlankLines(newSet);
        unnecessaryWordsFilter(newSet);
        return newSet;
    }

    // Фильтр ненужных слов
    public void unnecessaryWordsFilter(Collection<String> setOfWords) {
        List<String> listOfPath = new ArrayList<>();
        listOfPath.add("src/main/resources/list_of_words_for_filtration/english_words.txt");
        listOfPath.add("src/main/resources/list_of_words_for_filtration/russian_words.txt");
        listOfPath.add("src/main/resources/list_of_words_for_filtration/german_words.txt");

        for (String path : listOfPath) {
            Collection<String> filterWords = parseFiltrationFile(path);
            setOfWords.removeAll(filterWords);
        }
    }

    private Collection<String> parseFiltrationFile(String filterLanguageFileName) {
        List<String> filteredWords = null;
        try {
            filteredWords = Files.readAllLines(Paths.get(filterLanguageFileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filteredWords;
    }

    //Удаление пустого элемента
    public void deleteBlankLines(Collection<String> setOfWords) {
        setOfWords.removeIf(String::isEmpty);
    }

    public Collection<String> wordsToLowerCase(Collection<String> setOfWords) {
        return setOfWords.stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
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
}
