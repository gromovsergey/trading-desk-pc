package com.foros.session.channel.exceptions;

/**
 *
 * @author oleg_roshka
 */
public class ChannelNotFoundExpressionException extends ExpressionConversionException {
    /** Creates a new instance of ChannelNotFoundExpressionException */
    public ChannelNotFoundExpressionException() {
    }

    public ChannelNotFoundExpressionException(String message) {
        super(message);
    }

    public ChannelNotFoundExpressionException(String message, String name) {
        super(message, name);
    }

    public ChannelNotFoundExpressionException(Throwable cause) {
        super(cause);
    }

    public ChannelNotFoundExpressionException(String message, String name, Throwable cause) {
        super(message, name, cause);
    }
}
