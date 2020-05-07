package scraper;

import utils.Link;

public class FailedSite {
    private final Exception exception;
    private final Link link;

    public FailedSite(Exception exception, Link link) {
        this.exception = exception;
        this.link = link;
    }

    public Exception getException() {
        return exception;
    }

    public Link getLink() {
        return link;
    }
}
