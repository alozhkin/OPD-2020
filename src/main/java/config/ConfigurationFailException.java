package config;

public class ConfigurationFailException extends RuntimeException {
    public ConfigurationFailException() {
        super();
    }

    public ConfigurationFailException(String message) {
        super(message);
    }

    public ConfigurationFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationFailException(Throwable cause) {
        super(cause);
    }

    protected ConfigurationFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
