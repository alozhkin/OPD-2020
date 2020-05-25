package scraper;

/**
 * Exception that informs about critical error in Scraper.
 */
public class ScraperFailException extends RuntimeException {
    public ScraperFailException(Throwable cause) {
        super(cause);
    }
}
