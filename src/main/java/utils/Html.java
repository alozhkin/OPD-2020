package utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {
    private static final Html EMPTY_HTML = new Html("", new Link(""));

    private final String html;
    private final Link url;
    private final String lang;

    public Html(String html, @NotNull Link url) {
        this.html = html;
        this.url = url;
        this.lang = findLang(html);
    }

    // tries to parse encoding, if it is not possible uses UTF-8
    public static Html fromFile(Path path) throws IOException {
        String html = Files.readString(path, StandardCharsets.ISO_8859_1);
        String charset = getCharset(html);
        if (charset == null) {
            return new Html(Files.readString(path, StandardCharsets.UTF_8), new Link(path.toString()));
        } else {
            return new Html(Files.readString(path, Charset.forName(charset)), new Link(path.toString()));
        }
    }

    public static Html fromFile(Path path, Link domain) throws IOException {
        String html = Files.readString(path, StandardCharsets.ISO_8859_1);
        String charset = getCharset(html);
        if (charset == null) {
            return new Html(Files.readString(path, StandardCharsets.UTF_8), domain);
        } else {
            return new Html(Files.readString(path, Charset.forName(charset)), domain);
        }
    }

    // returns first charset of all in last meta tag with charset attr
    // cannot define if meta tag is incorrect and would not be parsed by browser.
    public static String getCharset(String html) {
        String tag = getTagFromHtml(html, "meta");
        String lang = null;
        if (tag != null) {
            lang = getAttrFromHtmlElement(tag, "charset");
        }
        return lang;
    }

    private static String findLang(String html) {
        String tag = getTagFromHtml(html, "html");
        String lang = null;
        if (tag != null) {
            lang = getAttrFromHtmlElement(tag, "lang");
        }
        return lang;
    }

    private static String getTagFromHtml(String html, String tag) {
        String htmlTagStrPattern = String.format("<\\s*%s[^><]*>"
                + "|<\\s*%s[^>]*>[^><]*<\\s*/\\s*%s\\s*>", tag, tag, tag);
        Pattern htmlTagPattern = Pattern.compile(htmlTagStrPattern);
        Matcher htmlTagMatcher = htmlTagPattern.matcher(html);
        if (htmlTagMatcher.find()) {
            return htmlTagMatcher.group();
        } else {
            return null;
        }
    }

    private static String getAttrFromHtmlElement(String el, String attr) {
        String attrStrPattern = String.format("%s\\s*=\\s*\"?\\s*[\\w\\d\\-]*\\s*\"?", attr);
        Pattern attrPattern = Pattern.compile(attrStrPattern);
        String value = null;
        Matcher langMatcher = attrPattern.matcher(el);
        if (langMatcher.find()) {
            var htmlAttr = langMatcher.group();
            value = htmlAttr.substring(htmlAttr.indexOf("=") + 1).replace("\\s", "").replace("\"", "");
        }
        return value;
    }

    public String getLang() {
        return lang;
    }

    public Link getUrl() {
        return url;
    }

    public static Html emptyHtml() {
        return EMPTY_HTML;
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