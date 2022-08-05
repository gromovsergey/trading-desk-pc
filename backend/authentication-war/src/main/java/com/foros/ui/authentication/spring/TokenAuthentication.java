package com.foros.ui.authentication.spring;

import com.foros.security.principal.Tokenable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class TokenAuthentication extends AbstractAuthenticationToken implements Tokenable {

    private String token;

    public TokenAuthentication(String token) {
        super(null);
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getName() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

}
