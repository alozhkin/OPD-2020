package crawler;

import config.ConfigurationFailException;
import config.ConfigurationUtils;
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
 * <ul><li>links that have path segment from {@link DefaultLinkFilter#ignoredLinks}
 * <li>links that have first path segment from {@link DefaultLinkFilter#ignoredLanguages} which is not matching
 * site.langs property
 * <li>links which file extension is not present in {@link DefaultLinkFilter#allowedFileExtensions}
 * <li>links with subdomains from {@link DefaultLinkFilter#ignoredSubdomains}
 * <li>links which host name is not a domain or subdomain of page url
 * <li>repeating links. Links are separated by its subdomains (not <i>"www"</i>), path segments and
 * selected query params from {@link DefaultLinkFilter#getContentParams(Link)}.
 * <li>links with fragment
 * <li>links with user info<ul/>
 */
public class DefaultLinkFilter implements LinkFilter {
    private static final Set<String> ignoredLanguages = new HashSet<>();
    private static final Set<String> allowedFileExtensions = new HashSet<>();
    private static final Set<String> ignoredLinks = new HashSet<>();
    private static final Set<String> ignoredSubdomains = new HashSet<>();

    static {
        ConfigurationUtils.parseResourceToCollection(
                "file_extensions.txt", allowedFileExtensions, DefaultLinkFilter.class
        );
        ConfigurationUtils.parseResourceToCollection("languages.txt", ignoredLanguages, DefaultLinkFilter.class);
        var ignoredLinksFormFile = new HashSet<String>();
        ConfigurationUtils.parseResourceToCollection(
                "ignored_links.txt", ignoredLinksFormFile, DefaultLinkFilter.class
        );
        for (String l : ignoredLinksFormFile) {
            ignoredLinks.add(l);
            for (String fe : allowedFileExtensions) {
                ignoredLinks.add(l + "." + fe);
            }
        }
        ConfigurationUtils.parseResourceToCollection(
                "ignored_subdomains.txt", ignoredSubdomains, DefaultLinkFilter.class
        );
    }

    private final Set<LinkIdentifiers> occurredLinks = ConcurrentHashMap.newKeySet();

    public DefaultLinkFilter() {
        if (allowedFileExtensions.isEmpty()) {
            throw new ConfigurationFailException("DefaultLinkFilter - Allowed file extensions are not found");
        }
    }

    /**
     * Adds typical home page names to occurred links
     */
    public void addDomain() {
        occurredLinks.add(new LinkIdentifiers(""));
        occurredLinks.add(new LinkIdentifiers("index"));
        for (String fileExtension : allowedFileExtensions) {
            occurredLinks.add(new LinkIdentifiers("/index." + fileExtension));
        }
    }

    @Override
    public Collection<Link> filter(@NotNull Collection<Link> links, Link currentLink) {
        Set<Link> res = new HashSet<>();
        occurredLinks.add(getLinkIdentifiers(currentLink));
        for (Link link : links) {
            var linkIdentifiers = getLinkIdentifiers(link);
            if (isLinkSuitable(link.fixWWW(), currentLink.fixWWW()) && isLinkNotOccurred(linkIdentifiers)) {
                res.add(link);
            }
        }
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
                var propertySplit = System.getProperty("site.langs").split(",");
                for (String lang : propertySplit) {
                    if (lang.equals(firstSegment)) return true;
                }
                return !ignoredLanguages.contains(firstSegment.toLowerCase());
            }
        }
        return true;
    }

    private boolean hasUsefulInfo(Link link) {
        var paths = link.getPath().split("/");
        for (String path : paths) {
            if (!path.equals("") && ignoredLinks.contains(path.toLowerCase())) return false;
        }
        var subdomains = link.getSubdomains();
        for (String subdomain : subdomains) {
            if (ignoredSubdomains.contains(subdomain.toLowerCase())) return false;
        }
        return true;
    }

    private boolean isFileExtensionSuitable(Link link) {
        var path = link.getPath();
        if (!path.equals("")) {
            var lastIndex = path.lastIndexOf('/');
            var lastSegment = path.substring(lastIndex);
            // last array part is file extension if array has size > 1
            var lastSegmentSplit = lastSegment.split("\\.");
            if (lastSegmentSplit.length == 1) {
                return true;
            } else {
                return allowedFileExtensions.contains(lastSegmentSplit[lastSegmentSplit.length - 1].toLowerCase());
            }
        } else {
            return true;
        }
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
                    || name.equals("page_id")
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
