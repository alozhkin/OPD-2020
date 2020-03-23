package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultLinkFilter implements LinkFilter {
    Set<String> visitedLinks = new HashSet<>();

    public Collection<Link> filter(@NotNull Collection<Link> links, String domain) {
        Set<Link> result = new HashSet<>();
        for (Link unUrl : links) {
            String url = unUrl.fixer().toLowerCase();
            if (url.contains(domain.toLowerCase()) && !url.contains("#") && !visitedLinks.contains(url)) {
                visitedLinks.add(url);
                result.add(new Link(url));
            }
        }
        return result;
    }
}
