package scraper;

/**
 * Exception, informing that Scraper do not scraped domain because of an error too many times in a row.
 * Number of times set in {@link spider.Spider}.
 */
public class TooManyFailsException extends RuntimeException {
    public TooManyFailsException() {
        super("too many fails");
    }
}
