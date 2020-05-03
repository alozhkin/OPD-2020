package splash;

import okhttp3.Request;

public interface SplashRequestFactory {
    Request getRequest(SplashRequestContext context);
}
