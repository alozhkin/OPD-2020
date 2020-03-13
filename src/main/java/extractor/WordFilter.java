package extractor;

import java.util.List;
import java.util.Set;

public interface WordFilter {
    List<String> filter(Set<String> words);
}
