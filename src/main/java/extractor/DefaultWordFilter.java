package extractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultWordFilter implements WordFilter {

    @Override
    public Collection<String> filter(Collection<String> words) throws IOException {
        Collection<String> newSet = setToLowerCase(punctuationMarkFilter(words));
        deleteBlankLines(newSet);
        unnecessaryWordsFilter(newSet);
        return newSet;
    }


    // Фильтр ненужных слов
    // Кпд данного метода не определен из-за кодировок

    public static void unnecessaryWordsFilter(Collection<String> set) throws IOException {
        String fileName = "src\\main\\resources\\ListOfWordsForFiltration.txt";

        String content = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).reduce("", String::concat);
        String[] stringsArray = content.split("\\s");
        Collection<String> filterWords = new HashSet<>(Arrays.asList(stringsArray));

        set.removeAll(filterWords);

    }

    //Удаление пустого элемента
    public static void deleteBlankLines(Collection<String> set) {
        set.removeIf(String::isEmpty);
    }

    public static Collection<String> setToLowerCase(Collection<String> set) {

        return set.stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
    }


    //Фильтр знаков препинания

    public static Collection<String> punctuationMarkFilter(Collection<String> set) {

        Collection<String> newSet = new HashSet<>();

        for (String setObj : set)
            newSet.add(delNoDigOrLet(setObj));

        return newSet;
    }

    // Метод удаления знаков препинания из строки
    // Кпд данного метода не определен из-за кодировок

    private static String delNoDigOrLet(String s) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }

        return sb.toString();
    }

    //Удаляет умляубля (äöü)
    // String result = s.replaceAll("\\W", "");

}
