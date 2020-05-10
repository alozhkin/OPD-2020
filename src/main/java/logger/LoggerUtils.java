package logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class LoggerUtils {
    private static final AtomicInteger pagesScraped = new AtomicInteger(0);
    public static Logger debugLog = LoggerFactory.getLogger("FILE");
    public static Logger consoleLog = LoggerFactory.getLogger("STDOUT");

    // prevents class instantiation
    private LoggerUtils() {}

    public static void logFileNotFound(String fileName, Class<?> c) {
        consoleLog.error(c.getSimpleName() + " - File " + fileName + " not found");
        debugLog.error(c.getSimpleName() + " - File " + fileName + " not found");
    }

    public static void logFileReadingFail(String fileName, Class<?> c) {
        consoleLog.error(c.getSimpleName() + " - File " + fileName + " reading fail");
        debugLog.error(c.getSimpleName() + " - File " + fileName + " reading fail");
    }

    public static void pageScraped() {
        pagesScraped.incrementAndGet();
    }

    public static int getPagesScraped() {
        return pagesScraped.get();
    }
}
