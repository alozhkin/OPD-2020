package crawler;

import config.ConfigurationFailException;
import config.ConfigurationUtils;
import logger.LoggerUtils;
import org.jetbrains.annotations.NotNull;
import utils.Link;
import utils.Parameter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultLinkFilter implements LinkFilter {
    private static final Set<String> languages = new HashSet<>();
    private static final Set<String> fileExtensions = new HashSet<>();
    private static final Set<String> ignoredLinks = new HashSet<>();

    private final Set<LinkIdentifiers> occurredLinks;

    static {
        ConfigurationUtils.parseResourceToCollection("languages.txt", languages, DefaultLinkFilter.class);
        ConfigurationUtils.parseResourceToCollection("file_extensions.txt", fileExtensions, DefaultLinkFilter.class);

        ConfigurationUtils.parseResourceToCollection("ignored_links.txt", ignoredLinks, DefaultLinkFilter.class);
    }

    public DefaultLinkFilter() {
        if (fileExtensions.isEmpty()) {
            throw new ConfigurationFailException("DefaultLinkFilter - Allowed file extensions are not found");
        }
        occurredLinks = ConcurrentHashMap.newKeySet();
    }

    //suggests that main page were visited
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
            var linkIdentifiers = new LinkIdentifiers(link.getPath(), getContentParams(link), link.getSubdomains());
            if (isLinkSuitable(link, domain) && isLinkNotOccurred(linkIdentifiers)) {
                res.add(link);
            }
        }
        LoggerUtils.debugLog.debug("Link filtration task completed");
        return res;
    }

    private boolean isLinkSuitable(Link link, Link domain) {
        return isOnSameDomain(link, domain)
                && hasNoFragment(link)
                && hasNoUserInfo(link)
                && hasRightLang(link)
                && hasUsefulInfo(link)
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
            var pathWithoutSlash = path.substring(1);
            var firstSegment = pathWithoutSlash.substring(0, indexOfSlash(pathWithoutSlash));
            if (!firstSegment.isEmpty()) {
                return System.getProperty("site.langs").contains(firstSegment) || !languages.contains(firstSegment);
            }
        }
        return true;
    }

    private boolean hasUsefulInfo(Link link) {
        var paths = link.getPath().split("/");
        for (String p : paths) {
            if (ignoredLinks.contains(p)) return false;
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

    private synchronized boolean isLinkNotOccurred(LinkIdentifiers linkIdentifiers) {
        var contains = occurredLinks.contains(linkIdentifiers);
        if (!contains) {
            occurredLinks.add(linkIdentifiers);
        }
        return !contains;
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

    private int indexOfSlash(String str) {
        var indexOf = str.indexOf('/');
        return indexOf != -1 ? indexOf : str.length();
    }

    // for link occurrence check
    private static class LinkIdentifiers {

        private final String path;
        private final Set<Parameter> params;
        private final Set<String> subDomains;

        public LinkIdentifiers(String path) {
            this.path = path;
            this.params = new HashSet<>();
            this.subDomains = new HashSet<>();
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
}
