package splash;

/**
 * Inform about permanent problem with splash connection unlike {@link scraper.ScraperConnectionException} which
 * reports about one-time problem
 */
public class SplashNotRespondingException extends RuntimeException {
    public SplashNotRespondingException() {
        super("Splash is not responding, 503 Service Unavailable");
    }
}
