package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultLinkFilter implements LinkFilter {

    public Set<Link> filter(@NotNull List<Link> links, String domain) {
        Set<Link> exit = new HashSet<>();
        for (Link url : links) {
            if (url.toString().contains(domain)) {
                exit.add(url);
            }
        }
        return exit;
    }
}
