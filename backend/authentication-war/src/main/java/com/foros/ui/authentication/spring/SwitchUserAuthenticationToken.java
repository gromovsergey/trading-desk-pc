package com.foros.ui.authentication.spring;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SwitchUserAuthenticationToken extends AbstractAuthenticationToken {

    private Long userId;

    public SwitchUserAuthenticationToken(Long userId) {
        super(null);
        this.userId = userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getUserId();
    }

    public Long getUserId() {
        return userId;
    }
}
