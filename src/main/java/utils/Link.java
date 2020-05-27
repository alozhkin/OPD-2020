package utils;

import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class that abstracts url and contains useful methods
 */
public class Link {
    private static final String DEFAULT_PROTOCOL = "http";
    private static final Pattern schemePattern = Pattern.compile("^[a-z][a-z0-9]*://");
    private HttpUrl httpUrl;
    private String strUrl;

    /**
     * Creates Link.
     * <p>
     * <ul><li>wrong url causes exception
     * <li>removes trailing slash
     * <li>if scheme is absent, uses http
     * <li>support non-ASCII characters
     * <li>domains encoded with <a href="https://en.wikipedia.org/wiki/Punycode">punycode</a>
     * <li>paths are decoded from <a href="https://en.wikipedia.org/wiki/Percent-encoding">percent encoding</a></ul>
     *
     * @param urlStr url (host is required)
     * @throws WrongFormedLinkException if url is not valid
     */
    public Link(@NotNull String urlStr) {
        try {
            httpUrl = HttpUrl.get(fix(urlStr));
        } catch (IllegalArgumentException e) {
            throw new WrongFormedLinkException(urlStr, e);
        }
    }

    private Link() {
        httpUrl = null;
    }

    private static String fix(String url) {
        if (url.isEmpty()) return url;
        var urlFixed = fixProtocol(url);
        return fixTrailingSlash(urlFixed);
    }

    private static String fixProtocol(String url) {
        if (schemePattern.matcher(url).find()) {
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

    /**
     * Empty link is link, that returns "" path and toString, -1 port and null in other cases
     *
     * @return link without url
     */
    public static Link createEmptyLink() {
        return new Link();
    }

    /**
     * Removes <i>"www."</i>, even when protocol specified
     *
     * @return link without <i>"www."</i> or <i>"www."</i> if link equals <i>"www."</i>
     */
    public Link fixWWW() {
        if (getHost().equals("www.")) return this;
        var fixed = getWithoutProtocol();
        if (fixed.startsWith("www.")) {
            fixed = fixed.substring(4);
        }
        return new Link(getScheme() + "://" + fixed);
    }

    /**
     * Return all key-value query params
     *
     * @return params
     */
    public Set<Parameter> getParams() {
        var res = new HashSet<Parameter>();
        var query = getQuery();
        if (query != null) {
            var querySplit = query.split("&");
            for (String parameter : querySplit) {
                var paramSplit = parameter.split("=");
                if (paramSplit.length == 2) {
                    var name = paramSplit[0];
                    var value = paramSplit[1];
                    res.add(new Parameter(name, value));
                }
            }
        }
        return res;
    }

    /**
     * Returns domains, ignores top-level, second-level domain and <i>"www."</i>
     *
     * @return domains without top-level, second level and <i>"www."</i>
     */
    public Set<String> getSubdomains() {
        var res = new HashSet<String>();
        var host = getHost();
        if (host == null) return new HashSet<>();
        var hostSplit = host.split("\\.");
        var levelsNumber = hostSplit.length;
        // ignore top-level and second-level domain
        for (int i = 0; i < levelsNumber - 2; i++) {
            // ignore www
            var subdomain = hostSplit[i];
            if (!subdomain.equals("www")) {
                res.add(subdomain);
            }
        }
        return res;
    }

    public String getWithoutQueryUserInfoAndFragment() {
        var sb = new StringBuilder();
        sb.append(getScheme()).append("://").append(getHost());
        var port = getPort();
        if (port != -1) {
            sb.append(":").append(port);
        }
        sb.append(getPath());
        return sb.toString();
    }

    public String getWithoutProtocol() {
        var url = new StringBuilder();
        var userInfo = getUserInfo();
        if (userInfo != null) {
            url.append(userInfo).append("@");
        }
        url.append(getHost());
        var port = getPort();
        if (port != -1) {
            url.append(":").append(port);
        }
        var path = getPath();
        if (!path.equals("")) {
            url.append(path);
        }
        var query = getQuery();
        if (query != null) {
            url.append("?").append(query);
        }
        var fragment = getFragment();
        if (fragment != null) {
            url.append("#").append(fragment);
        }
        return url.toString();
    }

    public String getAbsoluteURL() {
        if (httpUrl == null) return null;
        return toString();
    }

    public String getScheme() {
        if (httpUrl == null) return null;
        return httpUrl.scheme();
    }

    /**
     * Get user info "username:password" or "username" or null if username == ""
     *
     * @return user info or null
     */
    public String getUserInfo() {
        if (httpUrl == null) return null;
        var username = httpUrl.username();
        var password = httpUrl.password();
        var userInfo = new StringBuilder();
        if (!username.equals("")) {
            userInfo.append(username);
            if (!password.equals("")) {
                userInfo.append(":").append(password);
            }
            return userInfo.toString();
        } else {
            return null;
        }
    }

    public String getHost() {
        if (httpUrl == null) return null;
        return httpUrl.host();
    }

    /**
     * Return port, or -1 if it is not specified or matches 80, 443 - HttpUrl automatically ports
     *
     * @return port
     */
    public int getPort() {
        if (httpUrl == null) return -1;
        int port = httpUrl.port();
        if (port == 80 || port == 443) return -1;
        return port;
    }

    /**
     * Gets "" if path is absent.
     * Gets "/" + "path" if not.
     *
     * @return path
     */
    public String getPath() {
        if (httpUrl == null) return "";
        var pathSeg = httpUrl.pathSegments();
        if (pathSeg.size() == 1 && pathSeg.get(0).equals("")) {
            return "";
        } else {
            return "/" + String.join("/", pathSeg);
        }
    }

    public String getQuery() {
        if (httpUrl == null) return null;
        return httpUrl.query();
    }

    public String getFragment() {
        if (httpUrl == null) return null;
        return httpUrl.fragment();
    }

    @Override
    public String toString() {
        if (httpUrl == null) return "";
        if (strUrl == null) {
            strUrl = getScheme() + "://" + getWithoutProtocol();
        }
        return strUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(httpUrl, link.httpUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpUrl);
    }
}
