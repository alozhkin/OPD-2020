package spider;

import logger.LoggerUtils;
import utils.Link;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

/**
 * Class that processes html
 */
public class SiteTask {
    private final Context context;
    private final BlockingQueue<Link> linkQueue;
    private final Collection<String> resultWords;

    /**
     *
     * @param context contains behaviors
     * @param linkQueue would get all links from html
     * @param resultWords would get all words from html
     */
    SiteTask(Context context, BlockingQueue<Link> linkQueue, Collection<String> resultWords) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.resultWords = resultWords;
    }

    /**
     * Extracts links, runs them through filter and gives to collections, defined by constructor
     *
     * @param site html and all useful info
     */
    public void handleSite(Site site) {
        var html = site.getHtml();
        var initialLink = site.getInitialLink();
        if (!html.isLangRight()) {
            throw new HtmlLanguageException();
        }
        var htmlLink = html.getUrl();
        var links = context.crawl(html);
        var filteredLinks = context.filterLinks(links, htmlLink, initialLink);
        linkQueue.addAll(filteredLinks);
        var words = context.extract(html);
        var filteredWords = context.filterWords(words);
        LoggerUtils.debugLog.info("SiteTask - Completed " + htmlLink.toString());
        resultWords.addAll(filteredWords);
        LoggerUtils.pageScraped();
    }
}
