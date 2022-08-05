package com.foros.ui.authentication.spring;

import com.foros.model.security.AuthenticationToken;
import com.foros.security.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class ValidateTokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getName();
        AuthenticationToken authenticationToken = authenticationService.findAuthenticationToken(token);

        authentication.setAuthenticated(authenticationToken != null);

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
