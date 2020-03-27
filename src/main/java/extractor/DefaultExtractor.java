package extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Html;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class DefaultExtractor implements Extractor {

    public Collection<String> extract(Html html) {
        Document doc = Jsoup.parse(html.toString());
        String allInfo = doc.text();
        String[] stringsArray = allInfo.split("\\s");

        return new HashSet<>(Arrays.asList(stringsArray));
    }
}