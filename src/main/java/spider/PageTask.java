package spider;

import logger.LoggerUtils;
import utils.Html;
import utils.Link;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

/**
 * Class that processes html
 */
public class PageTask {
    private final Context context;
    private final BlockingQueue<Link> linkQueue;
    private final Collection<String> resultWords;

    /**
     * @param context contains behaviors
     * @param linkQueue accumulate all links from html
     * @param resultWords accumulate all words from html
     */
    PageTask(Context context, BlockingQueue<Link> linkQueue, Collection<String> resultWords) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.resultWords = resultWords;
    }

    /**
     * Extracts links/words, runs them through filters and adds to {@link PageTask#linkQueue}/
     * {@link PageTask#resultWords}
     *
     * @param page html and all useful info
     */
    public void handlePage(Page page) {
        var html = page.getHtml();
        var htmlLink = html.getUrl();
        var initialLink = page.getInitialLink();
        var frames = page.getFrames();
        if (!html.isLangRight()) {
            throw new HtmlLanguageException();
        }
        var links = context.crawl(html);
        for (Html frame : frames) {
            links.addAll(context.crawl(frame));
        }
        var filteredLinks = context.filterLinks(links, htmlLink, initialLink);
        linkQueue.addAll(filteredLinks);
        var words = context.extract(html);
        for (Html frame : frames) {
            words.addAll(context.extract(frame));
        }
        var filteredWords = context.filterWords(words);
        LoggerUtils.debugLog.info("PageTask - Completed {}", htmlLink.toString());
        resultWords.addAll(filteredWords);
        LoggerUtils.pageScraped();
    }
}
