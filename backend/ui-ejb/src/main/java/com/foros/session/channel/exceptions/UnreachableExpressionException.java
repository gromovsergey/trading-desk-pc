package com.foros.session.channel.exceptions;

/**
 *
 * @author oleg_roshka
 */
public class UnreachableExpressionException extends ExpressionConversionException {
    public UnreachableExpressionException() {
    }

    public UnreachableExpressionException(String message) {
        super(message);
    }

    public UnreachableExpressionException(String message, String name) {
        super(message, name);
    }

    public UnreachableExpressionException(Throwable cause) {
        super(cause);
    }

    public UnreachableExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
