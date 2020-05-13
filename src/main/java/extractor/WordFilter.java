package extractor;

import java.util.Collection;

public interface WordFilter {
    /**
     * Filters words relying on internal implementation
     *
     * @param words words to be filtered
     * @return filtered words
     */
    Collection<String> filter(Collection<String> words);
}
