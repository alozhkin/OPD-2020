package main;

import utils.Link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class SiteTask {
    private Context context;
    private Link link;
    private BlockingQueue<Link> linkQueue;

    public SiteTask(Context c,
                    Link site,
                    BlockingQueue<Link> q) {
        context = c;
        link = site;
        linkQueue = q;
    }

    public Collection<String> run() {
        try {
            Main.debugLog.info("Site " + link.toString() + " task start");
            var html = context.scrape(link);
            var links = context.crawl(html);
            var filteredLinks = context.filter(links, html.getUrl());
            linkQueue.addAll(filteredLinks);
            var words = context.extract(html);
            return context.filter(words);
        } catch (Exception e) {
            Main.consoleLog.error("SiteTask - Failed to run program: {}", e.toString());
            Main.debugLog.error("SiteTask - Failed to run program:", e);
            return new ArrayList<>();
        } finally {
            Main.debugLog.info("Site " + link.toString() + " task completed");
            Main.completedTaskCount.incrementAndGet();
        }
    }
}