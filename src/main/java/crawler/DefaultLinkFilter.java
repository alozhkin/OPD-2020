package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLinkFilter implements LinkFilter {
    private Set<Link> visitedLinks = ConcurrentHashMap.newKeySet();
    private static Set<String> languages;
    private static Set<String> fileExtensions;

    static {
        try {
            languages = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/languages.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            languages = new HashSet<>();
        }
        try {
            fileExtensions = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/file_extensions.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            fileExtensions = new HashSet<>();
        }
    }

    public Collection<Link> filter(@NotNull Collection<Link> links, String domain) {
        Set<Link> result = new HashSet<>();
        for (Link link : links) {
            String strLink = link.toString();
            if (strLink.contains(domain)
                    && !strLink.contains("#")
                    && !visitedLinks.contains(new Link(strLink))
                    && !hasWrongLang(link)
                    && isFileExtensionSuitable(link)) {
                result.add(new Link(strLink));
            }
        }
        visitedLinks.addAll(result);
        return result;
    }

    private boolean hasWrongLang(Link link) {
        var path = link.getPath();
        if (path != null) {
            var t = path.substring(1);
            var firstSegment = t.substring(0, indexOfSlash(t));
            if (!firstSegment.isEmpty()) {
                return !System.getProperty("site.langs").contains(firstSegment) && languages.contains(firstSegment);
            }
        }
        return false;
    }

    private boolean isFileExtensionSuitable(Link link) {
        var path = link.getPath();
        if (path != null) {
            var lastIndex = path.lastIndexOf('/');
            var lastSegment = path.substring(lastIndex);
            var splitted = lastSegment.split("\\.");
            if (splitted.length == 2) {
                return fileExtensions.contains(splitted[1]);
            }
        }
        return true;
    }

    private int indexOfSlash(String str) {
        var indexOf = str.indexOf('/');
        return indexOf != -1 ? indexOf : str.length();
    }
}
