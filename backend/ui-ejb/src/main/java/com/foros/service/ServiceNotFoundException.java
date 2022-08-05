package com.foros.service;

/**
 *
 * @author oleg_roshka
 */
public class ServiceNotFoundException extends RemoteServiceException {
    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }
}
