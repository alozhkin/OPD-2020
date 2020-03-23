package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class HTML {
    private String html;
    private Link url;

    public HTML(String html, Link url) {
        this.html = html;
        this.url = url;
    }

    public HTML(String html) {
        this.html = html;
    }

    public HTML(Path path) {
        try {
            this.html = Files.lines(path, StandardCharsets.UTF_8)
                    .reduce("", String::concat);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        HTML html1 = (HTML) o;
        return Objects.equals(html, html1.html) &&
                Objects.equals(url, html1.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(html, url);
    }
}
