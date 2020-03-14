package extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.HTML;
import util.Link;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class DefaultExtractor implements Extractor {

    public static void main(String[] args) {
        Link l = new Link("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)");
        HTML h = new HTML("",l);
       HashSet setOfWords = (HashSet) new DefaultExtractor().extract(h);
       System.out.println(setOfWords);
    }


    public Set<String> extract(HTML html) {

        /*Document doc = Jsoup.parse(html.toString());
        for (Element element : doc.select("*"))
            set.add(element.text());
        return set;*/
        Document doc = null;
        try {
            //doc = Jsoup.parse(new ByteArrayInputStream(html.toString().getBytes()), "windows-1251", html.getUrl().toString());
            doc = Jsoup.parse( new File("D:\\Users\\Kazuru\\Downloads\\Suka.html"), "windows-1251", html.getUrl().toString());
            //doc = Jsoup.connect("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String allInfo = doc.text();
        System.out.println(allInfo);
        String[] stringsArray;
        stringsArray = allInfo.split("\\s");
        // System.out.println(setOfWords);
        return new HashSet<>(Arrays.asList(stringsArray));
    }

}
