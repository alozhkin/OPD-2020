package config;

import logger.LoggerUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigurationUtils {
    // required properties. Would throw exception if absent
    private static final String GLOBAL_PROPERTIES_FILE_PATH = "properties/global.properties";
    // optional properties. Would log fail if absent
    private static final String LOCAL_PROPERTIES_FILE_PATH = "properties/local.properties";
    private static final String DATABASE_PROPERTIES_FILE_PATH = "properties/database.properties";

    // prevents class instantiation
    private ConfigurationUtils() {}

    public static void configure() {
        loadProperties();
        setConsoleEncoding();
    }

    public static void parseResourceToCollection(String fileName, Collection<String> collection, Class<?> c) {
        try (InputStream resource = ClassLoader.getSystemResourceAsStream(fileName)) {
            if (resource != null) {
                parseResource(resource, fileName, collection, c);
            } else {
                LoggerUtils.logFileNotFound(fileName, c);
            }
        } catch (FileNotFoundException e) {
            LoggerUtils.logFileNotFound(fileName, c);
        } catch (IOException e) {
            LoggerUtils.logFileReadingFail(fileName, c);
        }
    }

    private static void parseResource(InputStream res, String fileName, Collection<String> collection, Class<?> c) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(res))) {
            collection.addAll(br.lines().collect(Collectors.toSet()));
        } catch (IOException e) {
            LoggerUtils.logFileReadingFail(fileName, c);
        }
    }

    private static void loadProperties() {
        Properties necessaryProperties = ConfigurationUtils
                .parseRequiredPropertiesFromFiles(GLOBAL_PROPERTIES_FILE_PATH);

        if (necessaryProperties.isEmpty()) {
            throw new ConfigurationFailException("Configuration file " + GLOBAL_PROPERTIES_FILE_PATH + " is not found");
        }

        Properties optionalProperties = ConfigurationUtils.parseOptionalPropertiesFromFiles(
                LOCAL_PROPERTIES_FILE_PATH,
                DATABASE_PROPERTIES_FILE_PATH
        );

        Properties properties = new Properties();
        properties.putAll(necessaryProperties);
        properties.putAll(optionalProperties);

        for (String key : properties.stringPropertyNames()) {
            String value;
            if (isDriverProperty(key)) {
                value = getDriverValueByOS(key, properties);
            } else {
                value = properties.getProperty(key);
            }
            System.setProperty(key, value);
        }
    }

    // last properties files override first
    private static Properties parseRequiredPropertiesFromFiles(String... propertiesPaths) {
        var properties = new Properties();
        try {
            for (String path : propertiesPaths) {
                var res = parsePropertiesFromFile(path);
                if (res == null) {
                    throw new ConfigurationFailException("Configuration files are not found");
                } else {
                    properties.putAll(res);
                }
            }
        } catch (IOException e) {
            throw new ConfigurationFailException("Configuration files are not loaded", e);
        }
        return properties;
    }

    // last properties files override first
    private static Properties parseOptionalPropertiesFromFiles(String... propertiesPaths) {
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

    private static String getDriverValueByOS(String key, Properties properties) {
        return properties.getProperty(key) + "_" + OSValidator.getSystem();
    }

    private static boolean isDriverProperty(String key) {
        return key.equals("webdriver.chrome.driver");
    }
}
