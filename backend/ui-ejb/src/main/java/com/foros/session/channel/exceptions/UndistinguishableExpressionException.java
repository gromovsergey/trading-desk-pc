package com.foros.session.channel.exceptions;

/** @author oleg_roshka */
public class UndistinguishableExpressionException extends ExpressionConversionException {
    public UndistinguishableExpressionException() {
    }

    public UndistinguishableExpressionException(String message, String name) {
        super(message, name);
    }
}
