package com.foros.ui.authentication.spring;

import com.foros.security.AuthenticationService;
import com.foros.security.principal.Tokenable;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class SwitchUserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Tokenable tokenAuthentication = (Tokenable) SecurityContextHolder.getContext().getAuthentication();
        SwitchUserAuthenticationToken token = (SwitchUserAuthenticationToken) authentication;

        Object userId = token.getPrincipal();

        if (!(userId instanceof Long)) {
            throw new BadCredentialsException("Invalid credentials, user id is null" );
        }

        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();

        return getAuthentication(tokenAuthentication.getToken(), (Long) userId, details.getRemoteAddress());
    }

    private Authentication getAuthentication(String token, Long id, String remoteAddress) {
        try {
            return new TokenAuthentication(authenticationService.switchUser(token, id, remoteAddress));
        } catch (ConstraintViolationException e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SwitchUserAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
