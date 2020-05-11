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

/**
 * DefaultLinkFilter removes:
 * 1. Links that have path segment from {@link DefaultLinkFilter#ignoredLinks}
 * Links that have path segment from {@link DefaultLinkFilter#ignoredLanguages} which are not specified
 * in site.langs property
 * 2. Links which file extension is not present in {@link DefaultLinkFilter#allowedFileExtensions}
 * 3. Links with subdomains from {@link DefaultLinkFilter#ignoredSubdomains}
 * 4. Links which host name is not a domain or subdomain of page url
 * 5. Repeated links. Links are separated by its subdomains (not www), path segments and
 * selected query params from {@link DefaultLinkFilter#getContentParams(Link)}.
 * 6. Links with fragment
 * 7. Links with user info
 */
public class DefaultLinkFilter implements LinkFilter {
    private static final Set<String> ignoredLanguages = new HashSet<>();
    private static final Set<String> allowedFileExtensions = new HashSet<>();
    private static final Set<String> ignoredLinks = new HashSet<>();
    private static final Set<String> ignoredSubdomains = new HashSet<>();

    private final Set<LinkIdentifiers> occurredLinks;

    static {
        ConfigurationUtils.parseResourceToCollection("languages.txt", ignoredLanguages, DefaultLinkFilter.class);
        ConfigurationUtils.parseResourceToCollection("ignored_links.txt", ignoredLinks, DefaultLinkFilter.class);
        ConfigurationUtils.parseResourceToCollection(
                "file_extensions.txt", allowedFileExtensions, DefaultLinkFilter.class
        );
        ConfigurationUtils.parseResourceToCollection(
                "ignored_subdomains.txt", ignoredSubdomains, DefaultLinkFilter.class
        );
    }

    public DefaultLinkFilter() {
        if (allowedFileExtensions.isEmpty()) {
            throw new ConfigurationFailException("DefaultLinkFilter - Allowed file extensions are not found");
        }
        occurredLinks = ConcurrentHashMap.newKeySet();
    }

    /**
     * Adds typical home page names to occurred links
     */
    public void addDomain() {
        occurredLinks.add(new LinkIdentifiers(""));
        occurredLinks.add(new LinkIdentifiers("index"));
        for (String fe : allowedFileExtensions) {
            occurredLinks.add(new LinkIdentifiers("/index." + fe));
        }
    }

    @Override
    public Collection<Link> filter(@NotNull Collection<Link> links, Link currentLink) {
        Set<Link> res = new HashSet<>();
        occurredLinks.add(getLinkIdentifiers(currentLink));
        for (Link link : links) {
            var linkIdentifiers = getLinkIdentifiers(link);
            if (isLinkSuitable(link, currentLink) && isLinkNotOccurred(linkIdentifiers)) {
                res.add(link);
            }
        }
        LoggerUtils.debugLog.debug("DefaultLinkFilter - Link filtration task completed");
        return res;
    }

    @Override
    public Collection<Link> filter(@NotNull Collection<Link> links, Link currentLink, Link initialLink) {
        occurredLinks.add(getLinkIdentifiers(initialLink));
        return filter(links, currentLink);
    }

    private LinkIdentifiers getLinkIdentifiers(Link link) {
        return new LinkIdentifiers(link.getPath(), getContentParams(link), link.getSubdomains());
    }

    private boolean isLinkSuitable(Link link, Link currentLink) {
        return isOnSameDomain(link, currentLink)
                && hasNoFragment(link)
                && hasNoUserInfo(link)
                && hasRightLang(link)
                && hasUsefulInfo(link)
                && isFileExtensionSuitable(link);
    }

    private boolean isOnSameDomain(Link link, Link currentLink) {
        return link.getHost().contains(currentLink.getHost());
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
                return System.getProperty("site.langs").contains(firstSegment) ||
                        !ignoredLanguages.contains(firstSegment);
            }
        }
        return true;
    }

    private boolean hasUsefulInfo(Link link) {
        var paths = link.getPath().split("/");
        for (String path : paths) {
            if (ignoredLinks.contains(path)) return false;
        }
        var subdomains = link.getSubdomains();
        for (String subdomain : subdomains) {
            if (ignoredSubdomains.contains(subdomain)) return false;
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
                return allowedFileExtensions.contains(lastSegmentSplitted[lastSegmentSplitted.length - 1]);
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
            if (name.equals("id")
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
