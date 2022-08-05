package com.foros.rs.client;

public class RsNotAuthorizedException extends RsException {
    public RsNotAuthorizedException() {
        super("Access is denied");
    }

    public RsNotAuthorizedException(String message) {
        super(message);
    }
}
