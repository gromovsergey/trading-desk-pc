package com.foros.aspect.registry.find;

public class FindException extends RuntimeException {

    public FindException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindException(Throwable cause) {
        super(cause);
    }

}
