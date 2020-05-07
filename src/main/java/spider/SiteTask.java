package spider;

import logger.LoggerUtils;
import logger.Statistic;
import utils.Html;
import utils.Link;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class SiteTask {
    private final Context context;
    private final BlockingQueue<Link> linkQueue;
    private final Collection<String> resultWords;
            
    SiteTask(Context context, BlockingQueue<Link> linkQueue, Collection<String> resultWords) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.resultWords = resultWords;
    }

    public void consumeHtml(Html html) {
        if (!html.langRight()) {
            throw new HtmlLanguageException();
        }
        var link = html.getUrl();
        var links = context.crawl(html);
        var filteredLinks = context.filterLinks(links, link);
        linkQueue.addAll(filteredLinks);
        var words = context.extract(html);
        var filteredWords = context.filterWords(words);
        LoggerUtils.debugLog.info("SiteTask - Completed " + link.toString());
        resultWords.addAll(filteredWords);
        Statistic.siteScraped();
    }
}
