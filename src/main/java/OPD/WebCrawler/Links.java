package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;
import java.util.HashSet;

public class Links {//Временная версия HashSet преназначеая для хранения, дополнения и сверения всех URL сайта

    public HashSet<String> links;//Этот класс жедательно перенести на уровень повыше что бы Scraper имел к нему доступ

    public Links() {
        links = new HashSet<>();
    }

    public boolean contains(String URL) {
        return links.contains(URL);
    }

    public void add(@NotNull String i) {
        links.add(i);
    }

    public void addAll(@NotNull HashSet<String> i) {
        links.addAll(i);
    }

    public int size() {
        return links.size();
    }

    public void printALL() {
        for (String link : links) System.out.println(link);
    }

    public HashSet<String> get() {
        return links;
    }
}
