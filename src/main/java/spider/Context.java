package spider;

import utils.Html;
import utils.Link;

import java.util.Collection;

public interface Context {
    Html scrape(Link site);
    Collection<Link> crawl(Html html);
    Collection<Link> filterLinks(Collection<Link> links, Link domain);
    Collection<String> extract(Html html);
    Collection<String> filterWords(Collection<String> words);
    void quit();
}
