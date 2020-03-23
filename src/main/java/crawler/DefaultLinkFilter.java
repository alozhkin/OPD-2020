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
        for (Link link : links) {
            String strLink = link.toString().toLowerCase();
            if (strLink.contains(domain.toLowerCase()) && !strLink.contains("#") && !visitedLinks.contains(strLink)) {
                visitedLinks.add(strLink);
                result.add(new Link(strLink));
            }
        }
        return result;
    }
}
