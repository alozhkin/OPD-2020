package utils;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Link {
    private URI uri;
    private static final String DEFAULT_PROTOCOL = "http";

    // wrong url changes to ""
    // removes trailing slash
    public Link(String url) {
        if (url.equals("")) {
            uri = null;
        } else {
            try {
                uri = new URL(fix(url)).toURI();
            } catch (Exception e) {
                uri = null;
            }
        }
    }

    private Link(URI uri) {
        this.uri = uri;
    }

    private static String fix(String url) {
        if (url.isEmpty()) return url;
        var urlFixed1 = fixProtocol(url);
        return fixTrailingSlash(urlFixed1);
    }

    private static String fixProtocol(String url) {
        if (url.contains("://")) {
            return url;
        } else {
            return DEFAULT_PROTOCOL + "://" + url;
        }
    }

    private static String fixTrailingSlash(String url) {
        if (url.charAt(url.length() - 1) == '/') {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static Link createFileLink(Path path) {
        try {
            return new Link(path.toUri());
        } catch (Exception e) {
            return new Link("");
        }
    }

    public static Link createEmptyLink() {
        return new Link("");
    }

    public int length() {
        if (uri == null) return 0;
        return uri.toString().length();
    }

    public Set<Parameter> getParams() {
        var res = new HashSet<Parameter>();
        var query = getQuery();
        if (query != null) {
            var querySplitted = query.split("&");
            for (String parameter : querySplitted) {
                var paramSplitted = parameter.split("=");
                var name = paramSplitted[0];
                var value = paramSplitted[1];
                res.add(new Parameter(name, value));
            }
        }
        return res;
    }

    public String getWithoutQueryAndFragment() {
        var str = getScheme() + "://" + getHost();
        var port = getPort();
        if (port != -1) {
            str = str + ":" + port;
        }
        str += getPath();
        return str;
    }

    public String getWithoutProtocol() {
        var str = getHost();
        var port = getPort();
        if (port != -1) {
            str = str + ":" + port;
        }
        str += getPath();
        var query = getQuery();
        if (query != null) {
            str += query;
        }
        var fragment = getFragment();
        if (fragment != null) {
            str += fragment;
        }
        return str;
    }

    public String getAbsoluteURL() {
        if (uri == null) return null;
        return uri.toString();
    }

    public String getScheme() {
        if (uri == null) return null;
        return uri.getScheme();
    }

    public String getSchemeSpecificPart() {
        if (uri == null) return null;
        return uri.getSchemeSpecificPart();
    }

    public String getAuthority() {
        if (uri == null) return null;
        return uri.getAuthority();
    }

    public String getUserInfo() {
        if (uri == null) return null;
        return uri.getUserInfo();
    }

    public String getHost() {
        if (uri == null) return null;
        return uri.getHost();
    }

    public int getPort() {
        if (uri == null) return -1;
        return uri.getPort();
    }

    public String getPath() {
        if (uri == null) return "";
        return uri.getPath();
    }

    public String getQuery() {
        if (uri == null) return null;
        return uri.getQuery();
    }

    public String getFragment() {
        if (uri == null) return null;
        return uri.getFragment();
    }

    @Override
    public String toString() {
        if (uri == null) return "";
        return uri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(uri, link.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
