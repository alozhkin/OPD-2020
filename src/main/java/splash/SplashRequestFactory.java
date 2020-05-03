package splash;

import okhttp3.Request;
import utils.Link;

interface SplashRequestFactory {
    Request getRequest(Link link);
}
