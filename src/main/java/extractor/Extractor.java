package extractor;

import util.HTML;
import java.util.HashSet;


public interface Extractor {
    HashSet<String> extract(HTML html);
}
