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
            Main.debugLog.info("SiteTask - Start " + link.toString());
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
                return new ArrayList<>();
            }
            var html = context.scrape(link);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
                return new ArrayList<>();
            }
            var links = context.crawl(html);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
                return new ArrayList<>();
            }
            var filteredLinks = context.filter(links, html.getUrl());
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
                return new ArrayList<>();
            }
            linkQueue.addAll(filteredLinks);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
                return new ArrayList<>();
            }
            var words = context.extract(html);
            if (Thread.currentThread().isInterrupted()) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
                return new ArrayList<>();
            }
            Collection<String> filteredWords = context.filter(words);
            Main.debugLog.info("SiteTask - Completed " + link.toString());
            return filteredWords;
        } catch (WebDriverException e) {
            var cause = e.getCause();
            if (cause != null && cause.getClass() == InterruptedIOException.class) {
                Main.debugLog.info("SiteTask - Interrupted " + link.toString());
            } else if (e.getClass() == org.openqa.selenium.TimeoutException.class) {
                Main.debugLog.error("SiteTask - Loading timeout is over " + link.toString());
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
