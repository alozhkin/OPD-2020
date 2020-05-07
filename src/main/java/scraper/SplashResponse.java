package scraper;

class SplashResponse {
    private final String html;
    private final String url;

    public SplashResponse(String html, String url) {
        this.html = html;
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public String getUrl() {
        return url;
    }
}
