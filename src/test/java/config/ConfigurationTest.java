package config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationTest {

    @Test
    public void nonExistingPropertyFilesShouldThrowException() {
        assertThrows(ConfigurationFailException.class, () -> ConfigurationUtils.loadProperties("dummy_name"));
    }
}
