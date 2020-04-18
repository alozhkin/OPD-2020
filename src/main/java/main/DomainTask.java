package main;

import utils.Link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class DomainTask {
    public static AtomicLong submittedTasksCount = new AtomicLong(0);
    public static AtomicLong completedTaskCount = new AtomicLong(0);
    private final Context context;
    private final BlockingQueue<Link> linkQueue;
    ExecutorCompletionService<Collection<String>> cs;
    private final Link domain;

    public DomainTask(Context context,
                      BlockingQueue<Link> linkQueue,
                      ExecutorCompletionService<Collection<String>> cs,
                      Link domain) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.cs = cs;
        this.domain = domain;
    }

    boolean findTo(Collection<String> allWords) throws ExecutionException {
        Main.debugLog.info("Domain Task - start executing site " + domain);
        Set<Future<Collection<String>>> set = new HashSet<>();
        set.add(cs.submit(new SiteTask(context, domain, linkQueue)::run));
        submittedTasksCount.incrementAndGet();
        // order is important
        try {
            while (!Thread.currentThread().isInterrupted()
                    && (completedTaskCount.get() - submittedTasksCount.get() != 0 || linkQueue.size() != 0)) {
                var link = linkQueue.poll(50, TimeUnit.MILLISECONDS);
                if (link != null) {
                    set.add(cs.submit(new SiteTask(context, link, linkQueue)::run));
                    submittedTasksCount.incrementAndGet();
                }
                var wordsFuture = cs.poll(50, TimeUnit.MILLISECONDS);
                if (wordsFuture != null) {
                    set.remove(wordsFuture);
                    completedTaskCount.incrementAndGet();
                    allWords.addAll(wordsFuture.get());
                }
            }
        } catch (InterruptedException ignored) {}
        Main.debugLog.info("Domain Task - stop executing site " + domain);
        for (Future<Collection<String>> f : set) {
            f.cancel(true);
        }
        return false;
    }
}
