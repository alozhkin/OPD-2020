import config.ConfigurationFailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.util.Properties;
import main.WordsExtractor;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    public void localPropertiesShouldOverrideGlobal() throws IOException {
        File testDir = new File("src/test/resources/");
        File localProperties = File.createTempFile("local.test.properties", null, testDir);
        File globalProperties = File.createTempFile("global.test.properties", null, testDir);
        try (BufferedWriter globalWriter = new BufferedWriter(new FileWriter(globalProperties))) {
            globalWriter.write("one=2");
        }
        try (BufferedWriter localWriter = new BufferedWriter(new FileWriter(localProperties))) {
            localWriter.write("one=1");
        }
        
        Properties properties = WordsExtractor.loadProperties(globalProperties.getAbsolutePath(),
                localProperties.getAbsolutePath());

        localProperties.delete();
        globalProperties.delete();

        assertEquals("1", properties.getProperty("one"));
    }

    @Test
    public void nonExistingPropertyFilesShouldThrowException() {
        assertThrows(ConfigurationFailException.class, () -> WordsExtractor.loadProperties("dummy_name"));
    }
}
