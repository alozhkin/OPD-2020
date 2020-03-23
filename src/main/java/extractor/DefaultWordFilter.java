package extractor;

import java.util.Collection;

public class DefaultWordFilter implements WordFilter {

    @Override
    public Collection<String> filter(Collection<String> words) {
        return words;
    }
}
