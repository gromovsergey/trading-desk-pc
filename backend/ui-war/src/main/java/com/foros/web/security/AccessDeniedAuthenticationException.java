package com.foros.web.security;

import org.springframework.security.core.AuthenticationException;

public class AccessDeniedAuthenticationException extends AuthenticationException {
    public AccessDeniedAuthenticationException(Throwable t) {
        super(t.toString(), t);
    }
}
