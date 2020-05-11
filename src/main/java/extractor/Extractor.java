package extractor;

import utils.Html;

import java.util.Collection;

public interface Extractor {
    /**
     * Returns all words from html
     *
     * @param html html
     * @return all words
     */
    Collection<String> extract(Html html);
}
