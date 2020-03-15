package extractor;

import utils.HTML;

import java.util.Set;

public interface Extractor {
    Set<String> extract(HTML html);
}
