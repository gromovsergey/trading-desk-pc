package com.foros.action.birt;

public class BirtDispatcherException extends RuntimeException {
    public BirtDispatcherException(String message) {
        super(message);
    }

    public BirtDispatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public BirtDispatcherException(Throwable cause) {
        super(cause);
    }
}
