package logger;

import main.Main;

public class LoggerUtils {
    public static void logFileNotFound(String fileName, Class<?> c) {
        Main.consoleLog.error(c.getSimpleName() + " - File " + fileName + " not found");
        Main.debugLog.error(c.getSimpleName() + " - File " + fileName + " not found");
    }

    public static void logFileReadingFail(String fileName, Class<?> c) {
        Main.consoleLog.error(c.getSimpleName() + " - File " + fileName + " reading fail");
        Main.debugLog.error(c.getSimpleName() + " - File " + fileName + " reading fail");
    }
}
