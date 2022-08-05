package com.foros.session.channel.exceptions;

/**
 *
 * @author oleg_roshka
 */
public class ExpressionConversionException extends Exception {
    private String name;

    public ExpressionConversionException() {
    }

    public ExpressionConversionException(String message) {
        super(message);
    }

    public ExpressionConversionException(String message, String name) {
        super(message);
        this.name = name;
    }

    public ExpressionConversionException(Throwable cause) {
        super(cause);
    }

    public ExpressionConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpressionConversionException(String message, String name, Throwable cause) {
        super(message, cause);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
