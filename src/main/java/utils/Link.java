package utils;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class Link {
    private URI uri;
    private static final String DEFAULT_PROTOCOL = "http";

    // wrong url changes to ""
    // removes trailing slash
    public Link(String url) {
        try {
            uri = new URL(fix(url)).toURI();
        } catch (Exception e) {
            uri = URI.create("");
        }
    }

    private String fix(String url) {
        var urlFixed1 = fixProtocol(url);
        return fixTrailingSlash(urlFixed1);
    }

    private String fixProtocol(String url) {
        if (url.contains(":")) {
            return url;
        } else {
            return DEFAULT_PROTOCOL + "://" + url;
        }
    }

    private String fixTrailingSlash(String url) {
        if (url.charAt(url.length() - 1) == '/') {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public int length() {
        return uri.toString().length();
    }


    public String getAbsoluteURL() {
        return uri.toString();
    }

    public String getScheme() {
        return uri.getScheme();
    }

    public boolean isAbsolute() {
        return uri.isAbsolute();
    }

    public boolean isOpaque() {
        return uri.isOpaque();
    }

    public String getRawSchemeSpecificPart() {
        return uri.getRawSchemeSpecificPart();
    }

    public String getSchemeSpecificPart() {
        return uri.getSchemeSpecificPart();
    }

    public String getRawAuthority() {
        return uri.getRawAuthority();
    }

    public String getAuthority() {
        return uri.getAuthority();
    }

    public String getRawUserInfo() {
        return uri.getRawUserInfo();
    }

    public String getUserInfo() {
        return uri.getUserInfo();
    }

    public String getHost() {
        return uri.getHost();
    }

    public int getPort() {
        return uri.getPort();
    }

    public String getRawPath() {
        return uri.getRawPath();
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getRawQuery() {
        return uri.getRawQuery();
    }

    public String getQuery() {
        return uri.getQuery();
    }

    public String getRawFragment() {
        return uri.getRawFragment();
    }

    public String getFragment() {
        return uri.getFragment();
    }

    public int compareTo(URI that) {
        return uri.compareTo(that);
    }

    public String toASCIIString() {
        return uri.toASCIIString();
    }

    @Override
    public String toString() {
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
