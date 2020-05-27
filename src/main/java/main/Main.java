package main;

import config.ConfigurationUtils;
import database.Database;
import logger.LoggerUtils;
import spider.DefaultContextFactory;
import spider.OnSpiderChangesListener;
import spider.Spider;

public class Main {
    private static final String INPUT_PATH = "websites_data.csv";
    private static final String OUTPUT_PATH = "export.csv";

    // prevents class instantiation
    public static void main(String[] args) {
        start(INPUT_PATH, OUTPUT_PATH, null);
    }

    public static void start(String inputPath, String outputPath, OnSpiderChangesListener listener) {
        ConfigurationUtils.configure();
        LoggerUtils.debugLog.info("Main - START");
        var spider = new Spider(new DefaultContextFactory(), Database.newInstance());
        spider.setListener(listener);
        spider.scrapeFromCSVFile(inputPath, outputPath);
        LoggerUtils.debugLog.info("Main - {} pages were scraped in total", LoggerUtils.getPagesScraped());
        LoggerUtils.consoleLog.info("Main - {} pages were scraped in total", LoggerUtils.getPagesScraped());
    }
}