package splash;

public class SplashNotRespondingException extends RuntimeException {
    public SplashNotRespondingException() {
        super("Splash is not responding, 503 Service Unavailable");
    }
}
