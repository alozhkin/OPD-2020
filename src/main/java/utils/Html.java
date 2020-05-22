package utils;

import logger.LoggerUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that abstracts html and adds useful methods
 */
public class Html {
    private static final Pattern htmlTagPattern = Pattern.compile("<\\s*html[^><]*>"
            + "|<\\s*html[^>]*>[^><]*<\\s*/\\s*html\\s*>");
    private static final Pattern metaTagPattern = Pattern.compile("<\\s*meta[^><]*>"
            + "|<\\s*meta[^>]*>[^><]*<\\s*/\\s*meta\\s*>");
    private static final Pattern charsetAttrPattern = Pattern.compile("charset\\s*=\\s*\"?\\s*[\\w\\d\\-]*\\s*\"?");
    private static final Pattern contentAttrPattern = Pattern.compile("content\\s*=\\s*\"?\\s*[\\w\\d\\-]*\\s*\"?");
    private static final Pattern langAttrPattern    = Pattern.compile("lang\\s*=\\s*\"?\\s*[\\w\\d\\-]*\\s*\"?");
    private static final Pattern simpleLanguageAttrPattern = Pattern.compile("language");

    private final String html;
    private final Link url;
    private String lang;

    public Html(String html, @NotNull Link url) {
        this.html = html;
        this.url = url;
        try {
            this.lang = findLang(html);
        } catch (Exception e) {
            LoggerUtils.debugLog.error("HTML - Failed to parse lang on page {}", url);
            this.lang = "";
        }
    }

    public Html(String html) {
        this(html, Link.createEmptyLink());
    }

    /**
     *  Gets html from file. Tries to parse encoding, if it is not possible uses UTF-8.
     */
    public static Html fromFile(Path path) throws IOException {
        String html = Files.readString(path, StandardCharsets.ISO_8859_1);
        String charset = getCharset(html);
        if (charset == null) {
            return new Html(Files.readString(path, StandardCharsets.UTF_8));
        } else {
            return new Html(Files.readString(path, Charset.forName(charset)));
        }
    }

    /**
     *  Gets html from file. Sets url to domain. Tries to parse encoding, if it is not possible uses UTF-8.
     */
    public static Html fromFile(Path path, Link domain) throws IOException {
        String html = Files.readString(path, StandardCharsets.ISO_8859_1);
        String charset = getCharset(html);
        if (charset == null) {
            return new Html(Files.readString(path, StandardCharsets.UTF_8), domain);
        } else {
            return new Html(Files.readString(path, Charset.forName(charset)), domain);
        }
    }

    /**
     * Gets language of html if it is specified in <i>&lt;html lang="de"&gt;</i> or <i>&lt;meta lang="de"&gt;</i> or
     * <i>&lt;meta name="language" content="de"&gt;</i>.
     * <p>
     * Tries to find any meta tag that contains word "language" and then gets
     * content, so something like <i>&lt;meta language-not-an-attr content="de"&gt;</i> would give "de".
     * Cannot distinguish incorrect tags from correct.
     * @return lang
     */
    public String getLang() {
        return lang;
    }

    public Link getUrl() {
        return url;
    }

    /**
     * Compares lang with comma separated languages in site.langs property. Result of comparing for html
     * without language depends on reject.html.without.lang property
     *
     * @return {@code true} if language suitable, {@code false} if not
     */
    public boolean isLangRight() {
        var siteLangs = System.getProperty("site.langs");
        var htmlLang = lang;
        if (htmlLang != null) {
            for (String siteLang : siteLangs.split(",")) {
                if (siteLang.toLowerCase().equals(htmlLang.toLowerCase())) return true;
            }
        } else {
            return System.getProperty("reject.html.without.lang").equals("false");
        }
        return false;
    }

    // returns first charset of all in last meta tag with charset attr
    // cannot define if meta tag is incorrect and would not be parsed by browser.
    private static String getCharset(String html) {
        var tags = getTagsFromHtml(html, metaTagPattern);
        for (String tag : tags) {
            var charset = getAttrFromHtmlElement(tag, charsetAttrPattern);
            if (charset != null) return charset;
        }
        return null;
    }

    // use first html tag
    private static String findLang(String html) {
        var htmlTags = getTagsFromHtml(html, htmlTagPattern);
        if (htmlTags.size() != 0) {
            var tag = htmlTags.get(0);
            var lang = getAttrFromHtmlElement(tag, langAttrPattern);
            if (lang != null) return lang;
        }
        var metaTags = getTagsFromHtml(html, metaTagPattern);
        for (String tag : metaTags) {
            Matcher langMatcher = simpleLanguageAttrPattern.matcher(tag);
            if (langMatcher.find()) {
                return getAttrFromHtmlElement(tag, contentAttrPattern);
            }
        }
        return null;
    }

    private static List<String> getTagsFromHtml(String html, Pattern tagPattern) {
        Matcher tagMatcher = tagPattern.matcher(html);
        var res = new ArrayList<String>();
        while (tagMatcher.find()) {
            res.add(tagMatcher.group());
        }
        return res;
    }

    private static String getAttrFromHtmlElement(String el, Pattern attrPattern) {
        Matcher attrMatcher = attrPattern.matcher(el);
        if (attrMatcher.find()) {
            var htmlAttr = attrMatcher.group();
            return htmlAttr.substring(htmlAttr.indexOf("=") + 1).replace("\\s", "").replace("\"", "");
        }
        return null;
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