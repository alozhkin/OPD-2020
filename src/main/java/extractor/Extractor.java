package extractor;

import utils.Html;

import java.util.Collection;

public interface Extractor {
    Collection<String> extract(Html html);
}
