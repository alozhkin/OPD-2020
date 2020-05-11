package extractor;

import logger.LoggerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Html;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class DefaultExtractor implements Extractor {

    /**
     * Returns all text from html element splited with '\\s'
     *
     * @param html
     * @return all words
     */
    public Collection<String> extract(Html html) {
        Document doc = Jsoup.parse(html.toString());
        String allInfo = doc.text();
        String[] stringsArray = allInfo.split("\\s");
        LoggerUtils.debugLog.debug("DefaultExtractor - Extracting task completed");
        return new HashSet<>(Arrays.asList(stringsArray));
    }
}