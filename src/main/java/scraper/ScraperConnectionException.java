package scraper;

public class ScraperConnectionException extends RuntimeException {
    public ScraperConnectionException(String message) {
        super(message);
    }

    public ScraperConnectionException(Throwable cause) {
        super(cause);
    }
}
