package splash;

import okhttp3.Request;

public interface HtmlRendererRequestFactory {
    Request getRequest(SplashRequestContext context);
}
