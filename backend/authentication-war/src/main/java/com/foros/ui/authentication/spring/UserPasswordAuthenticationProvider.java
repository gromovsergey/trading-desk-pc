package com.foros.ui.authentication.spring;

import com.foros.security.AuthenticationService;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class UserPasswordAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = Logger.getLogger(UserPasswordAuthenticationProvider.class.getName());

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = (String) authentication.getPrincipal();

        Object password = authentication.getCredentials();

        if (!(password instanceof String)) {
            throw new BadCredentialsException("Password must be a String");
        }

        AdvancedWebAuthenticationDetails details = (AdvancedWebAuthenticationDetails) authentication.getDetails();

        Long lastUsedUserId = details.getLastUsedUserId();

        return getAuthentication(name, (String) password, lastUsedUserId, details.getRemoteAddress());
    }

    private Authentication getAuthentication(String name, String password, Long lastUsedUserId, String remoteAddress) {
        try {

            return new TokenAuthentication(
                    authenticationService.login(name, password, lastUsedUserId, remoteAddress)
            );

        } catch (ConstraintViolationException e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        } catch (Exception e) {
            String message = String.format("Unexpected error on login <%s>", name);
            logger.log(Level.INFO, message, e);
            throw new AuthenticationServiceException(message, e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
