package extractor;

import util.HTML;

import java.util.List;

public interface Extractor {
    List<String> extract(HTML html);
}
