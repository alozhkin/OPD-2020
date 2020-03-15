package main;

import config.ConfigurationFailException;
import database.DatabaseImpl;
import org.openqa.selenium.chrome.ChromeOptions;
import scraper.DefaultScraper;
import util.CSVParser;
import util.HTML;
import util.Link;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WordsExtractor {
    private static BlockingQueue<Link> linkQueue = new ArrayBlockingQueue<>(1000000);
    private static BlockingQueue<HTML> HTMLQueue = new ArrayBlockingQueue<>(1000000);
    private static Map<String, Integer> sitesId = new HashMap<>();
    private static Executor EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        configure();
        List<Link> links = new ArrayList<>();
        var csvParser = new CSVParser();
        Map<String, Integer> domainsIds = csvParser.getDomainsIds();
//        csvParser.parse("src/main/resources/websites_data.csv");
//        links = csvParser.getLinks();
        links.add(new Link("https://jsoup.org/"));
        for (Link link : links) {
            linkQueue.add(link);
            var parser = new SiteParser.Builder(linkQueue,
                    HTMLQueue,
                    new DatabaseImpl(domainsIds),
                    link.getDomain()).build();
            var scraper = new DefaultScraper(linkQueue, HTMLQueue);
            EXECUTOR_SERVICE.execute(parser::start);
            EXECUTOR_SERVICE.execute(scraper::start);
        }
    }

    private static void configure() {
        Properties properties = loadProperties("src/main/config/global.properties", "src/main/config/local.properties");

        String chromePath = properties.getProperty("chrome.path");
        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromePath);

        String chromeDriverPath = properties.getProperty("webdriver.chrome.driver");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    // last properties files override first
    // TODO решить проблему с тестированием private методов
    public static Properties loadProperties(String... propertiesPaths) {
        var res = new Properties();
        try {
            for (String path : propertiesPaths) {
                res.load(new FileInputStream(path));
            }
        } catch (FileNotFoundException e) {
            throw new ConfigurationFailException("Configuration files are not found", e);
        } catch (IOException e) {
            throw new ConfigurationFailException("Configuration files are not loaded", e);
        }
        return res;
    }
}
