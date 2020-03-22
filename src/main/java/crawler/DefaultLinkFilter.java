package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultLinkFilter implements LinkFilter {

    public Set<Link> filter(@NotNull List<Link> links, String domain) {

        Set<Link> exit = new HashSet<>();
        for (Link unUrl : links) {
            String url = unUrl.fixer();
            if (url.contains(domain) && !url.contains("#")) {
                if (formatChecker(url, domain)) {
                    exit.add(new Link(url.toLowerCase()));
                }
            }
        }
        return exit;
    }


    public boolean formatChecker(@NotNull String url, String domain) {

        String test = url.replaceAll(domain, "");
        if (test.equals(""))
            return false;

        return false;
    }
}
