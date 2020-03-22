package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.util.HashSet;
import java.util.Set;

public class DefaultLinkFilter implements LinkFilter {
    public Set<Link> filter(@NotNull Set<Link> links, String domain) {
        Set<Link> result = new HashSet<>();
        for (Link unUrl : links) {
            String url = unUrl.fixer().toLowerCase();
            if (url.contains(domain.toLowerCase()) && !url.contains("#")) {
                result.add(new Link(url));
            }
        }
        return result;
    }
}
