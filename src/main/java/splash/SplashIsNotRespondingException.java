package splash;

public class SplashIsNotRespondingException extends RuntimeException {
    public SplashIsNotRespondingException() {
        super("Splash is not responding, 503 Service Unavailable");
    }
}
