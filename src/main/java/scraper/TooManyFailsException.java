package scraper;

public class TooManyFailsException extends RuntimeException {
    public TooManyFailsException() {
        super("too many fails");
    }
}
