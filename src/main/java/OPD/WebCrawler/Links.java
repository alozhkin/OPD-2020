package OPD.WebCrawler;
import java.util.HashSet;

public class Links {

    public HashSet<String> links;//вынести во внешний класс

    public Links() {
        links = new HashSet<>();
    }

    public boolean contains(String URL) {
        return links.contains(URL);
    }

    public void addLinks(String link) {
        links.add(link);
    }

    public HashSet<String> getLinks() {
        return links;
    }
}
