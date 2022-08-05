package com.foros.util.xml.generator;

/**
 * Author: Boris Vanin
 */
public class GenerationException extends RuntimeException {

    public GenerationException() {
    }

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerationException(Throwable cause) {
        super(cause);
    }

}
