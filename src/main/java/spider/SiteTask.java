package spider;

import logger.LoggerUtils;
import utils.Html;
import utils.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class SiteTask {
    private final Context context;
    private final BlockingQueue<Link> linkQueue;

    SiteTask(Context c, BlockingQueue<Link> q) {
        context = c;
        linkQueue = q;
    }

    public Collection<String> run(Html html) {
        try {
            var link = html.getUrl();
            LoggerUtils.debugLog.info("SiteTask - Start " + link.toString());
            var links = context.crawl(html);
            var filteredLinks = context.filterLinks(links, link);
            linkQueue.addAll(filteredLinks);
            var words = context.extract(html);
            var filteredWords = context.filterWords(words);
            LoggerUtils.debugLog.info("SiteTask - Completed " + link.toString());
            return filteredWords;
        } catch (Exception e) {
            LoggerUtils.consoleLog.error("SiteTask - Failed to run program: {}", e.toString());
            LoggerUtils.debugLog.error("SiteTask - Failed to run program:", e);
        }
        return new ArrayList<>();
    }
}
