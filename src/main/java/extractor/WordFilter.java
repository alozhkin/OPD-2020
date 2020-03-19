package extractor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;


public interface WordFilter {
    Collection<String> filter(Collection<String> words) throws IOException;
}
