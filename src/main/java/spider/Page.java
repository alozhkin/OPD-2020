package spider;

import utils.Html;
import utils.Link;

import java.util.Collection;

/**
 * Class that contains all info required to process html properly
 */
public class Page {
    private final Html html;
    private final Link initialLink;
    private final Collection<Html> frames;

    public Page(Html html, Link initialLink, Collection<Html> frames) {
        this.html = html;
        this.initialLink = initialLink;
        this.frames = frames;
    }

    public Html getHtml() {
        return html;
    }

    public Link getInitialLink() {
        return initialLink;
    }

    public Collection<Html> getFrames() {
        return frames;
    }
}
