package spider;

/**
 * Is thrown if lang in html does not match with comma separated languages in site.langs property or
 * if reject.html.without.lang property set to true and html has no lang at all.
 */
public class HtmlLanguageException extends RuntimeException {}
