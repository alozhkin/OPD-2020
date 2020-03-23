package extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Html;
import utils.Link;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class DefaultExtractor implements Extractor {

    public static void main(String[] args) {
        Link l = new Link("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)");
        Html h = new Html("",l);
       HashSet setOfWords = (HashSet) new DefaultExtractor().extract(h);
       System.out.println(setOfWords);
    }


    public Set<String> extract(Html html) {

        /*Document doc = Jsoup.parse(html.toString());
        for (Element element : doc.select("*"))
            set.add(element.text());
        return set;*/
        Document doc = null;
        try {
            //doc = Jsoup.parse(new ByteArrayInputStream(html.toString().getBytes()), "windows-1251", html.getUrl().toString());
            doc = Jsoup.parse(new File("src/test/resources/wikipedia.html"), null, html.getUrl().toString());
            //doc = Jsoup.connect("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String allInfo = doc.text();
        String[] stringsArray;
        stringsArray = allInfo.split("\\s");
        return new HashSet<>(Arrays.asList(stringsArray));
    }

    public Set<String> extract(File htmlFile) throws IOException {
        Document doc = null;
        doc = Jsoup.parse(htmlFile, null, "");
        String allInfo = doc.text();
        String[] stringsArray = allInfo.split("\\s");
        return new HashSet<>(Arrays.asList(stringsArray));
    }
}
