package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLinkFilter implements LinkFilter {
    private Set<Link> visitedLinks = ConcurrentHashMap.newKeySet();

    public Collection<Link> filter(@NotNull Collection<Link> links, String domain) {
        Set<Link> result = new HashSet<>();
        for (Link link : links) {
            String strLink = link.toString().toLowerCase();
            if (strLink.contains(domain.toLowerCase())
                    && !strLink.contains("#")
                    && !visitedLinks.contains(new Link(strLink))) {
                result.add(new Link(strLink));
            }
        }
        visitedLinks.addAll(result);
        return result;
    }
}
