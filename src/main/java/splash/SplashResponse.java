package splash;

/**
 * Class that matches Splash JSON response
 */
public class SplashResponse {
    private final String html;
    private final String url;
    private final String[] frames;

    public SplashResponse(String html, String url, String[] frames) {
        this.html = html;
        this.url = url;
        this.frames = frames;
    }

    public String getHtml() {
        return html;
    }

    public String getUrl() {
        return url;
    }

    public String[] getFrames() {
        return frames;
    }
}
