package config;

import logger.LoggerUtils;

import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigurationUtils {
    public static void configure() {
        Properties properties = ConfigurationUtils.loadProperties("properties/global.properties",
                "properties/local.properties");

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.setProperty(key, value);
        }

        String chromePath = properties.getProperty("chrome.path");
        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromePath);

        ConfigurationUtils.setConsoleEncoding();
    }

    // last properties files override first
    public static Properties loadProperties(String... propertiesPaths) {
        var res = new Properties();
        try {
            for (String path : propertiesPaths) {
                var resource = ClassLoader.getSystemResourceAsStream(path);
                if (resource == null) throw new ConfigurationFailException("Configuration files are not found");
                res.load(resource);
                resource.close();
            }
        } catch (IOException e) {
            throw new ConfigurationFailException("Configuration files are not loaded", e);
        }
        return res;
    }

    public static void setConsoleEncoding() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    public static String parseDatabaseUrl() {
        Properties properties = loadProperties("properties/database.properties");
        return properties.getProperty("url");
    }

    public static void parseResourceToCollection(String fileName, Collection<String> collection, Class c) {
        var resource = ClassLoader.getSystemResourceAsStream(fileName);
        if (resource != null) {
            parseResource(resource, fileName, collection, c);
        } else {
            LoggerUtils.logFileNotFound(fileName, c);
        }
    }

    private static void parseResource(InputStream resource, String fileName, Collection<String> collection, Class c) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource))) {
            collection.addAll(br.lines().collect(Collectors.toSet()));
        } catch (IOException e) {
            LoggerUtils.logFileReadingFail(fileName, c);
        }
    }
}
