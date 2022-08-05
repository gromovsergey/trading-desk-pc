package com.foros.rs.client;

public class RsException extends RuntimeException {
    public RsException(String message) {
        super(message);
    }

    public RsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RsException(Throwable cause) {
        super(cause);
    }
}
