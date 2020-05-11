package splash;

import okhttp3.Request;

/**
 * Splash is ruled by HTTP API. It is behavior is ruled with every request. SplashRequestFactory subclasses would set its own
 * settings to Splash, making code flexible.
 */
public interface SplashRequestFactory {
    /**
     *
     * @param context credentials and variables, that are not responsible for settings
     * @return full ready request to Splash
     */
    Request getRequest(DefaultSplashRequestContext context);
}
