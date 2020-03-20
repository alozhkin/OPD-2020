package crawler;

import utils.Link;

import java.util.List;
import java.util.Set;

public interface LinkFilter {
    Set<Link> filter(List<Link> links, String domain);
}
