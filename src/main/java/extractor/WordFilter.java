package extractor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;


public interface WordFilter {
    Collection<String> filter(HashSet<String> words) throws IOException;
}
