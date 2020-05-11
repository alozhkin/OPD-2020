package spider;

import utils.Link;

/**
 * Class that contains site that was not scraped due to error and all information that useful for understanding why
 */
public class FailedSite {
    private final Exception exception;
    private final Link link;

    /**
     *
     * @param exception that lead to inability to scrape site
     * @param link site that was not scraped
     */
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
