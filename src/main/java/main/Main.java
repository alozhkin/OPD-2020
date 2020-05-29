package main;

import config.ConfigurationUtils;
import database.Database;
import logger.LoggerUtils;
import spider.DefaultContextFactory;
import spider.OnSpiderChangesListener;
import spider.Spider;

public class Main {
    private static final String INPUT_PATH = "websites_data.csv";
    private static final String OUTPUT_PATH = "";
    private static final String DATABASE_PATH = "websites.db";

    // prevents class instantiation
    public static void main(String[] args) {
        start(INPUT_PATH, OUTPUT_PATH, DATABASE_PATH, null);
    }

    /**
     * Starts scraping words
     *
     * @param inputPath    path to the file where the target sites are located
     * @param outputPath   path in which should to put the result of scraping
     * @param databasePath path where the database should be stored
     * @param listener     listener of the events of the spider so
     *                     that you can get information about the progress of the program
     */
    public static void start(String inputPath, String outputPath,
                             String databasePath, OnSpiderChangesListener listener) {
        ConfigurationUtils.configure();
        LoggerUtils.debugLog.info("Main - START");
        var spider = new Spider(new DefaultContextFactory(), Database.newInstance(databasePath));
        spider.setListener(listener);
        spider.scrapeFromCSVFile(inputPath, outputPath);
        LoggerUtils.debugLog.info("Main - {} pages were scraped in total", LoggerUtils.getPagesScraped());
        LoggerUtils.consoleLog.info("Main - {} pages were scraped in total", LoggerUtils.getPagesScraped());
    }
}