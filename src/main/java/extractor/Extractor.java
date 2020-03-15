package extractor;

import utils.Html;

import java.util.Set;

public interface Extractor {
    Set<String> extract(Html html);
}
