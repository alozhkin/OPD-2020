package logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
    public static Logger debugLog = LoggerFactory.getLogger("FILE");
    public static Logger consoleLog = LoggerFactory.getLogger("STDOUT");

    public static void logFileNotFound(String fileName, Class<?> c) {
        consoleLog.error(c.getSimpleName() + " - File " + fileName + " not found");
        debugLog.error(c.getSimpleName() + " - File " + fileName + " not found");
    }

    public static void logFileReadingFail(String fileName, Class<?> c) {
        consoleLog.error(c.getSimpleName() + " - File " + fileName + " reading fail");
        debugLog.error(c.getSimpleName() + " - File " + fileName + " reading fail");
    }
}
