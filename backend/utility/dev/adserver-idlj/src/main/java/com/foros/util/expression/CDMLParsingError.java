package com.foros.util.expression;

/**
 *
 * @author oleg_roshka
 */
public class CDMLParsingError extends Exception {
    /** Creates a new instance of CDMLParsingError */
    public CDMLParsingError() {
    }

    public CDMLParsingError(String message) {
        super(message);
    }

    public CDMLParsingError(Throwable cause) {
        super(cause);
    }

    public CDMLParsingError(String message, Throwable cause) {
        super(message, cause);
    }
}
