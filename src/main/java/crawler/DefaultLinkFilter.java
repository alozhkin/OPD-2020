package crawler;

import main.Main;
import org.jetbrains.annotations.NotNull;
import utils.Link;
import utils.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLinkFilter implements LinkFilter {
    private final Set<LinkIdentifiers> occurredLinks;
    private static Set<String> languages;
    private static Set<String> fileExtensions;

    //suggests that main page were visited
    public DefaultLinkFilter() {
        occurredLinks = ConcurrentHashMap.newKeySet();
    }

    static {
        try {
            languages = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/languages.txt")));
        } catch (IOException e) {
            Main.consoleLog.error("DefaultLinkFilter - Failed to get languages from the file: {}", e.toString());
            Main.debugLog.error("DefaultLinkFilter - Failed to get languages from the file:", e);
            languages = new HashSet<>();
        }
        try {
            fileExtensions = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources/file_extensions.txt")));
        } catch (IOException e) {
            Main.consoleLog.error("DefaultLinkFilter - Failed to get file extensions from the file: {}", e.toString());
            Main.debugLog.error("DefaultLinkFilter - Failed to get file extensions from the file:", e);
            fileExtensions = new HashSet<>();
        }
    }

    // for link occurrence check
    private static class LinkIdentifiers {

        private final String path;
        private final Set<Parameter> params;
        private final Set<String> subDomains;

        public LinkIdentifiers(String path) {
            this.path = path;
            params = new HashSet<>();
            subDomains = new HashSet<>();
        }

        public LinkIdentifiers(String path, Set<Parameter> params, Set<String> subDomains) {
            this.path = path;
            this.params = params;
            this.subDomains = subDomains;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LinkIdentifiers that = (LinkIdentifiers) o;
            return Objects.equals(path, that.path) &&
                    Objects.equals(params, that.params) &&
                    Objects.equals(subDomains, that.subDomains);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, params, subDomains);
        }

        @Override
        public String toString() {
            return "RelativeURL{" +
                    "path='" + path + '\'' +
                    ", params=" + params +
                    ", subdomains=" + subDomains +
                    '}';
        }
    }

    public void addDomain() {
        occurredLinks.add(new LinkIdentifiers(""));
        occurredLinks.add(new LinkIdentifiers("index"));
        for (String fe : fileExtensions) {
            occurredLinks.add(new LinkIdentifiers("/index." + fe));
        }
    }

    public Collection<Link> filter(@NotNull Collection<Link> links, Link domain) {
        Set<Link> res = new HashSet<>();
        for (Link link : links) {
            if (isLinkSuitable(link, domain)
                    && isLinkNotOccurred(new LinkIdentifiers(link.getPath(), getContentParams(link), link.getSubdomains()))
            ) {
                res.add(link);
            }
        }
        Main.debugLog.debug("Link filtration task completed");
        return res;
    }

    private boolean isLinkSuitable(Link link, Link domain) {
        return isOnSameDomain(link, domain)
                && hasNoFragment(link)
                && hasNoUserInfo(link)
                && hasRightLang(link)
                && isFileExtensionSuitable(link);
    }

    private boolean isOnSameDomain(Link link, Link domain) {
        return link.getHost().contains(domain.getHost());
    }

    private boolean hasNoFragment(Link link) {
        return link.getFragment() == null;
    }

    private boolean hasNoUserInfo(Link link) {
        return link.getUserInfo() == null;
    }

    private boolean hasRightLang(Link link) {
        var path = link.getPath();
        if (!path.equals("")) {
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
        if (!path.equals("")) {
            var lastIndex = path.lastIndexOf('/');
            var lastSegment = path.substring(lastIndex);
            // last array part is file extension if array has size > 1
            var lastSegmentSplitted = lastSegment.split("\\.");
            if (lastSegmentSplitted.length == 1) {
                return true;
            } else {
                return fileExtensions.contains(lastSegmentSplitted[lastSegmentSplitted.length - 1]);
            }
        }
        return true;
    }

    private Set<Parameter> getContentParams(Link link) {
        var params = link.getParams();
        var res = new HashSet<Parameter>();
        for (Parameter param : params) {
            var name = param.getName().toLowerCase();
            if (name.contains("id")
                    || name.equals("content")
                    || name.equals("page")
                    || name.equals("objectpath")) {
                res.add(param);
            }
        }
        return res;
    }

    private synchronized boolean isLinkNotOccurred(LinkIdentifiers linkIdentifiers) {
        var contains = occurredLinks.contains(linkIdentifiers);
        if (!contains) {
            occurredLinks.add(linkIdentifiers);
        }
        return !contains;
    }

    private int indexOfSlash(String str) {
        var indexOf = str.indexOf('/');
        return indexOf != -1 ? indexOf : str.length();
    }
}
