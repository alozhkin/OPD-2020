package extractor;

import main.Main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultWordFilter implements WordFilter {

    private Collection<String> filteredWords = getFilterWords();

    @Override
    public Collection<String> filter(Collection<String> words) {
        Main.debugLog.debug("Filtration task started");
        Collection<String> newSet = wordsToLowerCase(punctuationMarkFilter(words));
        deleteBlankLines(newSet);
        unnecessaryWordsFilter(newSet);
        Main.debugLog.info("Filtration task completed");
        return newSet;
    }

    // Фильтр ненужных слов
    public void unnecessaryWordsFilter(Collection<String> setOfWords) {
        setOfWords.removeAll(filteredWords);
        Main.debugLog.debug("Unnecessary words have been removed");
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
        Main.debugLog.debug("Punctuation has been removed");
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
        List<String> listOfPaths = new ArrayList<>();
        listOfPaths.add("src/main/resources/list_of_words_for_filtration/english_words.txt");
        listOfPaths.add("src/main/resources/list_of_words_for_filtration/russian_words.txt");
        listOfPaths.add("src/main/resources/list_of_words_for_filtration/german_words.txt");
        try {
            for (String path : listOfPaths) {
               filteredWords.addAll(Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            //e.toString() will print only what exception has been thrown
            //for example, java.lang.ArithmeticException: / by zero
            Main.consoleLog.error("DefaultWordFilter - Failed to parse file with filter words: {}",e.toString());
            //will write to the file exception with stacktrace
            //for example, java.lang.ArithmeticException: / by zero
            //              at Test.main(Test.java:9)
            Main.debugLog.error("DefaultWordFilter - Failed to parse file with filter words:", e);
        }
        return filteredWords;
    }
}