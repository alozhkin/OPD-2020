package spider;

import logger.LoggerUtils;
import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class DomainTask {
    private final Context context;
    private final BlockingQueue<Link> linkQueue;
    private final ExecutorCompletionService<Collection<String>> cs;
    private final Link domain;

    DomainTask(Context context,
               BlockingQueue<Link> linkQueue,
               ExecutorCompletionService<Collection<String>> cs,
               Link domain) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.cs = cs;
        this.domain = domain;
    }

    void findTo(Collection<String> allWords) {
        LoggerUtils.debugLog.info("Domain Task - Start executing site " + domain);
        Set<Future<Collection<String>>> futures = new HashSet<>();
        futures.add(cs.submit(new SiteTask(context, domain, linkQueue)::run));
        try {
            while (!Thread.currentThread().isInterrupted() && futures.size() != 0) {
                var link = linkQueue.poll(50, TimeUnit.MILLISECONDS);
                if (link != null) {
                    futures.add(cs.submit(new SiteTask(context, link, linkQueue)::run));
                }
                var wordsFuture = cs.poll(50, TimeUnit.MILLISECONDS);
                if (wordsFuture != null) {
                    futures.remove(wordsFuture);
                    allWords.addAll(wordsFuture.get());
                }
            }
        } catch (ExecutionException e) {
            LoggerUtils.debugLog.error("Domain Task - Failed on site " + domain, e);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } finally {
            for (Future<Collection<String>> f : futures) {
                f.cancel(true);
            }
            context.quit();
            LoggerUtils.debugLog.info("Domain Task - Stop executing site " + domain);
        }
    }
}
