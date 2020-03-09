package extractor;

import java.util.List;

public interface WordFilter {
    List<String> filter(List<String> words);
}
