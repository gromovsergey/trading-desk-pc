package com.foros.service;

import javax.ejb.ApplicationException;

/**
 *
 * @author oleg_roshka
 */
@ApplicationException(rollback = true)
public class RemoteServiceException extends Exception {
    /** 
     * Creates a new instance of RemoteServiceException 
     */
    public RemoteServiceException() {
    }

    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(Throwable cause) {
        super(cause);
    }

    public RemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
