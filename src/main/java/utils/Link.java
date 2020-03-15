package utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class Link {
    private String absoluteURL;

    public Link(String absoluteURL) {
        this.absoluteURL = absoluteURL;
    }

    public String getDomain() {
        try {
            var uri = new URI(absoluteURL);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return absoluteURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(absoluteURL, link.absoluteURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(absoluteURL);
    }
}
