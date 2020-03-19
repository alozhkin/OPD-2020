package extractor;

import util.HTML;

import java.util.Collection;



public interface Extractor {
    Collection<String> extract(HTML html);
}
