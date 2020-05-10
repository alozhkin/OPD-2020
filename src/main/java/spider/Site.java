package spider;

import utils.Html;
import utils.Link;

public class Site {
    private final Html html;
    private final Link initialLink;

    public Site(Html html, Link initialLink) {
        this.html = html;
        this.initialLink = initialLink;
    }

    public Html getHtml() {
        return html;
    }

    public Link getInitialLink() {
        return initialLink;
    }
}
