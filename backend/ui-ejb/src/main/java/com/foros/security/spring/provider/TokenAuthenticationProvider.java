package com.foros.security.spring.provider;

import com.foros.model.security.AuthenticationToken;
import com.foros.security.AuthenticationService;
import com.foros.security.principal.ApplicationPrincipalFactory;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return getAuthentication(
                (String) authentication.getPrincipal(),
                (WebAuthenticationDetails) authentication.getDetails()
        );
    }

    private Authentication getAuthentication(String token, WebAuthenticationDetails details) {
        try {
            AuthenticationToken authenticationToken = authenticationService.findAuthenticationToken(token);

            if (authenticationToken != null) {
                return ApplicationPrincipalFactory.createPrincipalByToken(authenticationToken, details.getRemoteAddress());
            }

            throw new AuthenticationServiceException("Token not authenticated");
        } catch (ConstraintViolationException e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
