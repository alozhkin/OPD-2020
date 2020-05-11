package scraper;

/**
 * Inform about one-time problem with splash connection unlike {@link splash.SplashNotRespondingException} which
 * reports about permanent problem
 */
public class ScraperConnectionException extends RuntimeException {
    public ScraperConnectionException(String message) {
        super(message);
    }

    public ScraperConnectionException(Throwable cause) {
        super(cause);
    }
}
