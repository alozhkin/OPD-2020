package config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigurationUtils {
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
}
