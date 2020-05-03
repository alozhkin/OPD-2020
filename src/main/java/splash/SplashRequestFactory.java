package splash;

import http_client.Request;
import utils.Link;

interface SplashRequestFactory {
    Request getRequest(Link link);
}
