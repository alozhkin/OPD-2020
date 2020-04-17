package main;

import utils.Link;

import java.util.Collection;
import java.util.concurrent.*;

import static main.Main.submittedTasksCount;
import static main.Main.completedTaskCount;

public class DomainTask {
    private Context context;
    private BlockingQueue<Link> linkQueue;
    ExecutorCompletionService<Collection<String>> cs;
    private Link domain;

    public DomainTask(Context context,
                      BlockingQueue<Link> linkQueue,
                      ExecutorCompletionService<Collection<String>> cs,
                      Link domain) {
        this.context = context;
        this.linkQueue = linkQueue;
        this.cs = cs;
        this.domain = domain;
    }

    boolean findTo(Collection<String> allWords) throws InterruptedException, ExecutionException {
        cs.submit(new SiteTask(context, domain, linkQueue)::run);
        submittedTasksCount.incrementAndGet();
        // order is important
        while (completedTaskCount.get() - submittedTasksCount.get() != 0 || linkQueue.size() != 0) {
            var link = linkQueue.poll(50, TimeUnit.MILLISECONDS);
            if (link != null) {
                cs.submit(new SiteTask(context, link, linkQueue)::run);
                submittedTasksCount.incrementAndGet();
            }
            var wordsFuture = cs.poll(50, TimeUnit.MILLISECONDS);
            if (wordsFuture != null) {
                allWords.addAll(wordsFuture.get());
            }
        }
        return false;
    }
}
