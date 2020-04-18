package main;

import org.openqa.selenium.WebDriverException;
import utils.Link;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class SiteTask {
    private final Context context;
    private final Link link;
    private final BlockingQueue<Link> linkQueue;

    SiteTask(Context c,
             Link site,
             BlockingQueue<Link> q) {
        context = c;
        link = site;
        linkQueue = q;
    }

    public Collection<String> run() {
        try {
            Main.debugLog.info("Site " + link.toString() + " task start");
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
                return new ArrayList<>();
            }
            var html = context.scrape(link);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
                return new ArrayList<>();
            }
            var links = context.crawl(html);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
                return new ArrayList<>();
            }
            var filteredLinks = context.filter(links, html.getUrl());
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
                return new ArrayList<>();
            }
            linkQueue.addAll(filteredLinks);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
                return new ArrayList<>();
            }
            var words = context.extract(html);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
                return new ArrayList<>();
            }
            Collection<String> filteredWords = context.filter(words);
            Main.debugLog.info("Site " + link.toString() + " task completed");
            return filteredWords;
        } catch (WebDriverException e) {
            if (e.getCause().getClass() == InterruptedIOException.class) {
                Main.debugLog.info("Site " + link.toString() + " task interrupted");
            } else {
                Main.consoleLog.error("SiteTask - Webdriver fail: {}", e.toString());
                Main.debugLog.error("SiteTask - Webdriver fail:", e);
            }
        } catch (Exception e) {
            Main.consoleLog.error("SiteTask - Failed to run program: {}", e.toString());
            Main.debugLog.error("SiteTask - Failed to run program:", e);
        }
        return new ArrayList<>();
    }
}
