package utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Html {
    private String html;
    private Link url;

    public Html(String html, @NotNull Link url) {
        this.html = html;
        this.url = url;
    }

    public Link getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return html;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Html html1 = (Html) o;
        return Objects.equals(html, html1.html) &&
                Objects.equals(url, html1.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(html, url);
    }
}
