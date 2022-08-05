package com.foros.service;

/**
 *
 * @author oleg_roshka
 */
public class RemoteServiceRegistrationException extends RemoteServiceException {
    public RemoteServiceRegistrationException() {
    }

    public RemoteServiceRegistrationException(String message) {
        super(message);
    }

    public RemoteServiceRegistrationException(Throwable cause) {
        super(cause);
    }

    public RemoteServiceRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
