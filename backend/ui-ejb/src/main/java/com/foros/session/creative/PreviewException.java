package com.foros.session.creative;

import javax.ejb.ApplicationException;

@ApplicationException
public class PreviewException extends RuntimeException {
    public PreviewException(String message) {
        super(message);
    }

    public PreviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreviewException(Throwable cause) {
        super(cause);
    }
}
