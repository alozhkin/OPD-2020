package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class Links {

    public HashSet<String> links;//вынести во внешний класс

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
