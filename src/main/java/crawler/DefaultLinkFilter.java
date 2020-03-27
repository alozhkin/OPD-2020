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
    private Set<Link> occurredLinks = ConcurrentHashMap.newKeySet();
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
            if (isLinkSuitable(link, domain) && isNotOccurred(link)) {
                result.add(link);
            }
        }
        return result;
    }

    private boolean isLinkSuitable(Link link, String domain) {
        return isOnSameDomain(link, domain)
                && hasNoFragment(link)
                && hasNoUserInfo(link)
                && hasRightLang(link)
                && isFileExtensionSuitable(link);
    }

    private boolean isOnSameDomain(Link link, String domain) {
        return link.getHost().contains(domain);
    }

    private boolean hasNoFragment(Link link) {
        return link.getFragment() == null;
    }

    private boolean hasNoUserInfo(Link link) {
        return link.getUserInfo() == null;
    }

    private boolean hasRightLang(Link link) {
        var path = link.getPath();
        if (path != null) {
            var t = path.substring(1);
            var firstSegment = t.substring(0, indexOfSlash(t));
            if (!firstSegment.isEmpty()) {
                return System.getProperty("site.langs").contains(firstSegment) || !languages.contains(firstSegment);
            }
        }
        return true;
    }

    private boolean isFileExtensionSuitable(Link link) {
        var path = link.getPath();
        if (path != null) {
            var lastIndex = path.lastIndexOf('/');
            var lastSegment = path.substring(lastIndex);
            // since in the url path a dot can only indicate a separator between the name and the file extension,
            // if we can split path into two parts, then second path is file extension
            var splitted = lastSegment.split("\\.");
            if (splitted.length == 2) {
                return fileExtensions.contains(splitted[1]);
            }
        }
        return true;
    }

    private synchronized boolean isNotOccurred(Link link) {
        var contains = occurredLinks.contains(link);
        if (!contains) {
            occurredLinks.add(link);
        }
        return !contains;
    }

    private int indexOfSlash(String str) {
        var indexOf = str.indexOf('/');
        return indexOf != -1 ? indexOf : str.length();
    }
}
