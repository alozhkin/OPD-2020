package config;

public class ConfigurationFailException extends RuntimeException {

    public ConfigurationFailException(String message) {
        super(message);
    }

    public ConfigurationFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
