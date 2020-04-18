package utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        var  tags = getTagsFromHtml(html, "meta");
        if (tags.size() != 0) {
            for (String tag : tags) {
                var charset = getAttrFromHtmlElement(tag, "charset");
                if (charset != null) return charset;
            }
        }
        return null;
    }

    // use first html tag
    private static String findLang(String html) {
        var htmlTags = getTagsFromHtml(html, "html");
        if (htmlTags.size() != 0) {
            var tag = htmlTags.get(0);
            var lang = getAttrFromHtmlElement(tag, "lang");
            if (lang != null) return lang;
        }
        var metaTags = getTagsFromHtml(html, "meta");
        for (String tag : metaTags) {
            String attrStrPattern = "language";
            Pattern attrPattern = Pattern.compile(attrStrPattern);
            Matcher langMatcher = attrPattern.matcher(tag);
            if (langMatcher.find()) {
                return getAttrFromHtmlElement(tag, "content");
            }

        }
        return null;
    }

    private static List<String> getTagsFromHtml(String html, String tag) {
        String htmlTagStrPattern = String.format("<\\s*%s[^><]*>"
                + "|<\\s*%s[^>]*>[^><]*<\\s*/\\s*%s\\s*>", tag, tag, tag);
        Pattern htmlTagPattern = Pattern.compile(htmlTagStrPattern);
        Matcher htmlTagMatcher = htmlTagPattern.matcher(html);
        var res = new ArrayList<String>();
        while (htmlTagMatcher.find()) {
            res.add(htmlTagMatcher.group());
        }
        return res;
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