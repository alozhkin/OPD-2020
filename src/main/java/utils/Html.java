package utils;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html {
    private static Html EMPTY_HTML = new Html("", new Link(""));

    private String html;
    private Link url;
    private String lang;

    public Html(String html, @NotNull Link url) {
        this.html = html;
        this.url = url;
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

    public Link getUrl() {
        return url;
    }

    String setDomain(Link domain) {
        this.url = domain;
    }

    public String getLang() {
        if (lang == null) {
            lang = Jsoup.parse(this.html, url.toString()).selectFirst("html").attr("lang");
        }
        return lang;
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
