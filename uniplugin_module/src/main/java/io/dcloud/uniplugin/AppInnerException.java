package io.dcloud.uniplugin;

public class AppInnerException extends Exception {
    public AppInnerException() {
    }

    public AppInnerException(String message) {
        super(message);
    }

    public AppInnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppInnerException(Throwable cause) {
        super(cause);
    }

    public AppInnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
