package extractor;

import java.util.Collection;

public interface WordFilter {
    /**
     * Filters words
     *
     * @param words
     * @return filtered words
     */
    Collection<String> filter(Collection<String> words);
}
