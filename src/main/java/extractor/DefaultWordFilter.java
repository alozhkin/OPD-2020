package extractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DefaultWordFilter implements WordFilter {

   public static void main(String[] args) throws IOException {
        HashSet set = new HashSet<String>();
        set.add("галлаграфический:");
        set.add("on");
        set.add("жопе,");
        set.add("in");
        set.add("Кемрово.");
        String s = "Кола,";
        //System.out.println(set.toString());
        new DefaultWordFilter().filter(set);
        //System.out.println(set.toString());
        Collection newSet = new DefaultWordFilter().filter(set);
        System.out.println(set);
        System.out.println(newSet);
    }

    @Override
    public Collection<String> filter(HashSet<String> words) throws IOException {
        HashSet newSet = PunctuationMarkFilter(words);
        UnnecessaryWordsFilter(newSet);
        return newSet;
    }


    // Фильтр ненужных слов
    // Кпд данного метода не определен из-за кодировок

    public static void UnnecessaryWordsFilter(HashSet<String> set) throws IOException {
        String fileName = "src\\main\\resources\\ListOfWordsForFiltration.txt";

        String content = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).reduce("", String::concat);

        String[] stringsArray = content.split("\\s");

        HashSet<String> filterWords = new HashSet<> (Arrays.asList(stringsArray));

        set.removeAll(filterWords);

    }

    //Фильтр знаков препинания

    public static HashSet<String> PunctuationMarkFilter(HashSet<String> set) throws IOException {
        HashSet<String> newSet = new HashSet<>();
        for (String setObj : set)
        newSet.add(delNoDigOrLet(setObj));
        return newSet;
    }

    // Метод удаления знаков препинания из строки
    // Кпд данного метода не определен из-за кодировок

    private static String delNoDigOrLet (String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character .isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    //Удаляет умляубля (äöü)
    // String result = s.replaceAll("\\W", "");

}
