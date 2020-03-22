package crawler;

import utils.Link;

import java.util.List;

public interface LinkFilter {
    List<Link> filter(List<Link> links, String domain);
}
