package config;

import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigurationUtils {
    public static void configure() {
        Properties properties = ConfigurationUtils.loadProperties("src/main/config/global.properties",
                "src/main/config/local.properties");

        String chromePath = properties.getProperty("chrome.path");
        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromePath);

        String chromeDriverPath = properties.getProperty("webdriver.chrome.driver");
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        String projectPath = properties.getProperty("project.path");
        System.setProperty("project.path", projectPath);

        ConfigurationUtils.setConsoleEncoding();
    }

    // last properties files override first
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

    public static void setConsoleEncoding() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }

    public static String parseDatabaseProperties() {
        Properties properties = loadProperties("src/main/config/database.properties");
        return properties.getProperty("url");
    }
}
