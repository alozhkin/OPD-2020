package config;

import logger.LoggerUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Class responsible for properties configuration
 * There are two types of property files: <b>required</b> throw {@link ConfigurationFailException} if file is absent and
 * <b>optional</b> (only would log fail)
 * {@throws ConfigurationFailException}
 */
public class ConfigurationUtils {
    // required properties
    private static final String GLOBAL_PROPERTIES_FILE_PATH = "properties/global.properties";
    // optional properties
    private static final String LOCAL_PROPERTIES_FILE_PATH = "properties/local.properties";
    private static final String DATABASE_PROPERTIES_FILE_PATH = "properties/database.properties";

    // prevents class instantiation
    private ConfigurationUtils() {}

    /**
     * Loads properties to {@link System}, they can be obtained using the method {@code System.getProperty()}
     * should be called at the beginning of the program
     */
    public static void configure() {
        loadProperties();
        setConsoleEncoding();
    }

    /**
     * Adds to collection all lines from resource
     *
     * @param fileName name of file with resources
     * @param collection collection to which the result will be sent
     * @param c class that call the method (will be used in logging)
     */
    public static void parseResourceToCollection(String fileName, Collection<String> collection, Class<?> c) {
        try (InputStream resource = ClassLoader.getSystemResourceAsStream(fileName)) {
            if (resource != null) {
                collection.addAll(parseResource(resource, fileName, c));
            } else {
                LoggerUtils.logFileNotFound(fileName, c);
            }
        } catch (IOException e) {
            LoggerUtils.debugLog.error("ConfigurationUtils - Resource {} closing error", fileName, e);
        }
    }

    private static Collection<String> parseResource(InputStream res, String fileName, Class<?> c) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(res))) {
            return br.lines().collect(Collectors.toSet());
        } catch (IOException e) {
            LoggerUtils.logFileReadingFail(fileName, c);
            return new ArrayList<>();
        }
    }

    private static void loadProperties() {
        Properties requiredProperties = ConfigurationUtils.parseRequiredPropsFromFiles(GLOBAL_PROPERTIES_FILE_PATH);

        if (requiredProperties.isEmpty()) {
            throw new ConfigurationFailException("Configuration file " + GLOBAL_PROPERTIES_FILE_PATH + " is not found");
        }

        Properties optionalProperties = ConfigurationUtils.parseOptionalPropsFromFiles(
                LOCAL_PROPERTIES_FILE_PATH,
                DATABASE_PROPERTIES_FILE_PATH
        );

        Properties properties = new Properties();
        properties.putAll(requiredProperties);
        properties.putAll(optionalProperties);

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.setProperty(key, value);
        }
    }

    private static Properties parseRequiredPropsFromFiles(String... propertiesPaths) {
        var properties = new Properties();
        try {
            for (String path : propertiesPaths) {
                var fileProperties = parsePropertiesFromFile(path);
                if (fileProperties == null) {
                    throw new ConfigurationFailException("Configuration files are not found");
                } else {
                    properties.putAll(fileProperties);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationFailException("Configuration files are not loaded", e);
        }
        return properties;
    }

    private static Properties parseOptionalPropsFromFiles(String... propertiesPaths) {
        var properties = new Properties();
        for (String path : propertiesPaths) {
            try {
                var res = parsePropertiesFromFile(path);
                if (res == null) {
                    LoggerUtils.logFileNotFound(path, ConfigurationUtils.class);
                } else {
                    properties.putAll(parsePropertiesFromFile(path));
                }
            } catch (IOException e) {
                LoggerUtils.logFileReadingFail(path, ConfigurationUtils.class);
            }
        }
        return properties;
    }

    private static Properties parsePropertiesFromFile(String propertiesPath) throws IOException {
        try (InputStream resource = ClassLoader.getSystemResourceAsStream(propertiesPath)) {
            var res = new Properties();
            if (resource == null) return null;
            res.load(resource);
            return res;
        }
    }

    private static void setConsoleEncoding() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
    }
}
