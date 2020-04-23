package main;

import logger.LoggerUtils;
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
            LoggerUtils.debugLog.info("SiteTask - Start " + link.toString());
            checkIfInterrupted();
            var html = context.scrape(link);
            checkIfInterrupted();
            var links = context.crawl(html);
            checkIfInterrupted();
            var filteredLinks = context.filterLinks(links, html.getUrl());
            checkIfInterrupted();
            linkQueue.addAll(filteredLinks);
            checkIfInterrupted();
            var words = context.extract(html);
            checkIfInterrupted();
            Collection<String> filteredWords = context.filterWords(words);
            checkIfInterrupted();
            LoggerUtils.debugLog.info("SiteTask - Completed " + link.toString());
            return filteredWords;
        } catch (InterruptedException e) {
            LoggerUtils.debugLog.info("SiteTask - Interrupted " + link.toString());
        } catch (WebDriverException e) {
            var cause = e.getCause();
            if (cause != null && cause.getClass() == InterruptedIOException.class) {
                LoggerUtils.debugLog.info("SiteTask - Interrupted " + link.toString());
            } else if (e.getClass() == org.openqa.selenium.TimeoutException.class) {
                LoggerUtils.debugLog.error("SiteTask - Loading timeout is over " + link.toString());
            } else {
                LoggerUtils.consoleLog.error("SiteTask - Webdriver fail: {}", e.toString());
                LoggerUtils.debugLog.error("SiteTask - Webdriver fail:", e);
            }
        } catch (Exception e) {
            LoggerUtils.consoleLog.error("SiteTask - Failed to run program: {}", e.toString());
            LoggerUtils.debugLog.error("SiteTask - Failed to run program:", e);
        }
        return new ArrayList<>();
    }

    private void checkIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }
}
