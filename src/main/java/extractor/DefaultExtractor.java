package extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Html;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExtractor implements Extractor {

    private final Logger log = LoggerFactory.getLogger("extractor");

    public Collection<String> extract(Html html) {
        log.info("extracting process started");

        Document doc = Jsoup.parse(html.toString());
        String allInfo = doc.text();
        String[] stringsArray = allInfo.split("\\s");

        log.info("extracting process finished");
        return new HashSet<>(Arrays.asList(stringsArray));
    }
}
