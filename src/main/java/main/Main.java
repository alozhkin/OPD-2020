package main;

import config.ConfigurationUtils;
import database.Database;
import logger.LoggerUtils;
import spider.DefaultContextFactory;
import spider.Spider;

public class Main {
    // in seconds
    private static final String INPUT_PATH = "src/main/resources/websites_data_short.csv";
    private static final String OUTPUT_PATH = "export.csv";

    public static void main(String[] args) {
        ConfigurationUtils.configure();
        LoggerUtils.debugLog.info("Main - START");
        var spider = new Spider(new DefaultContextFactory(), Database.newInstance());
        spider.scrapeFromCSVFile(INPUT_PATH, OUTPUT_PATH);
    }
}