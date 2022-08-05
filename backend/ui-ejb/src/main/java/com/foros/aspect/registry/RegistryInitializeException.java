package com.foros.aspect.registry;

public class RegistryInitializeException extends RuntimeException {

    public RegistryInitializeException(String message) {
        super(message);
    }

    public RegistryInitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryInitializeException(Throwable cause) {
        super(cause);
    }

}
