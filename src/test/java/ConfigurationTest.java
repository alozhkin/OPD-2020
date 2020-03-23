import config.ConfigurationFailException;
import config.ConfigurationUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        
        Properties properties = ConfigurationUtils.loadProperties(globalProperties.getAbsolutePath(),
                localProperties.getAbsolutePath());

        localProperties.delete();
        globalProperties.delete();

        assertEquals("1", properties.getProperty("one"));
    }

    @Test
    public void nonExistingPropertyFilesShouldThrowException() {
        assertThrows(ConfigurationFailException.class, () -> ConfigurationUtils.loadProperties("dummy_name"));
    }
}
