package spider;

import utils.Link;

/**
 * Class for page that was not scraped due to error and all information that useful for understanding why
 */
public class FailedPage {
    private final Exception exception;
    private final Link link;

    /**
     *
     * @param exception that lead to inability to scrape site
     * @param link page that was not scraped
     */
    public FailedPage(Exception exception, Link link) {
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
