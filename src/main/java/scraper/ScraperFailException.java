package scraper;

public class ScraperFailException extends RuntimeException {
    public ScraperFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
