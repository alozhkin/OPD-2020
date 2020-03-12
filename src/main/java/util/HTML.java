package util;

import java.util.Objects;

public class HTML {
    private String html;
    private Link url;
    private int size;
    private boolean isEmpty;
    public HTML(String html, Link url) {
        this.html = html;
        this.url = url;
        size = html.length();
        isEmpty = html.isEmpty();
    }

    public int size(){return size;}

    public Link getUrl() {
        return url;
    }

    public static HTML getEmptySource(){
        return new HTML("", new Link(""));
    }

    public boolean isEmpty(){
        return isEmpty;
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
