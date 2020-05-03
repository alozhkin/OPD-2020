package splash;

import okhttp3.Request;

interface SplashRequestFactory {
    Request getRequest(SplashRequestContext context);
}
