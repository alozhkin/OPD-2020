package extractor;

import util.HTML;

import java.util.Set;

public interface Extractor {
    Set<String> extract(HTML html);
}
