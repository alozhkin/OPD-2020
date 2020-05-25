package splash;

import okhttp3.Request;

/**
 * Splash is ruled by HTTP API and is very flexible, because it allows to set settings with every request.
 * SplashRequestFactory subclasses store settings that good in different situations, making code flexible.
 */
public interface SplashRequestFactory {
    /**
     * @param context credentials and variables
     * @return full ready request to Splash
     */
    Request getRequest(DefaultSplashRequestContext context);
}
