package utils;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.util.Objects;

public class Html {
    private String html;
    private Link url;
    private String language;

    public Html(String html, @NotNull Link url) {
        this.html = html;
        this.url = url;
        this.language = Jsoup.parse(this.html, url.toString()).selectFirst("html").attr("lang");
    }

    public Link getUrl() {
        return url;
    }

    public String getLanguage() {
        return language;
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
