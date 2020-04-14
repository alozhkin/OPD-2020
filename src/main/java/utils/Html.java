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
    private String lang;

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
    private static String getCharset(String html) {
        // <meta attr=value(/)>
        String metaTagStrPattern = "<\\s*meta\\s+[\\w\\s=\\-\";/]*/?\\s*>"
                // <meta attr=value></meta>
                + "|<\\s*meta\\s+[\\w\\s=\\-\";/]*>.*<\\s*/\\s*meta\\s*>";
        Pattern metaTagPattern = Pattern.compile(metaTagStrPattern);
        Matcher metaTagMatcher = metaTagPattern.matcher(html);
        // charset = ...
        String charsetStrPattern = "charset\\s*=\\s*\"?\\s*[\\w\\d\\-]*\\s*\"?";
        Pattern charsetPattern = Pattern.compile(charsetStrPattern);
        String charset = null;
        while (metaTagMatcher.find()) {
            var metaTag = metaTagMatcher.group();
            Matcher charsetMatcher = charsetPattern.matcher(metaTag);
            if (charsetMatcher.find()) {
                var charsetAttr = charsetMatcher.group();
                charset = charsetAttr.substring(charsetAttr.indexOf("=") + 1).replace("\\s", "").replace("\"", "");
            }
        }
        return charset;
    }

    private static String findLang(String html) {
        // <html attr=value(/)>
        String htmlTagStrPattern = "<\\s*html\\s+[\\w\\s=\\-\";/]*/?\\s*>"
                // <html attr=value></meta>
                + "|<\\s*html\\s+[\\w\\s=\\-\";/]*>.*<\\s*/\\s*html\\s*>";
        Pattern htmlTagPattern = Pattern.compile(htmlTagStrPattern);
        Matcher htmlTagMatcher = htmlTagPattern.matcher(html);
        // lang = ...
        String langStrPattern = "lang\\s*=\\s*\"?\\s*[\\w\\d\\-]*\\s*\"?";
        Pattern langPattern = Pattern.compile(langStrPattern);
        String aLang = null;
        while (htmlTagMatcher.find()) {
            var htmlTag = htmlTagMatcher.group();
            Matcher langMatcher = langPattern.matcher(htmlTag);
            if (langMatcher.find()) {
                var langAttr = langMatcher.group();
                aLang = langAttr.substring(langAttr.indexOf("=") + 1).replace("\\s", "").replace("\"", "");
            }
        }
        return aLang;
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