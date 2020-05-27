package logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class LoggerUtils {
    public static Logger debugLog = LoggerFactory.getLogger("FILE");
    public static Logger consoleLog = LoggerFactory.getLogger("STDOUT");
    private static final AtomicInteger pagesScraped = new AtomicInteger(0);

    // prevents class instantiation
    private LoggerUtils() {}

    public static void logFileNotFound(String fileName, Class<?> c) {
        consoleLog.error("{} - File {} not found", c.getSimpleName(), fileName);
        debugLog.error("{} - File {} not found", c.getSimpleName(), fileName);
    }

    public static void logFileReadingFail(String fileName, Class<?> c) {
        consoleLog.error("{} - File {} reading fail", c.getSimpleName(), fileName);
        debugLog.error("{} - File {} reading fail", c.getSimpleName(), fileName);
    }

    public static void pageScraped() {
        pagesScraped.incrementAndGet();
    }

    public static int getPagesScraped() {
        return pagesScraped.get();
    }

    public static Logger getUILogger() {
        return consoleLog;
    }
}
