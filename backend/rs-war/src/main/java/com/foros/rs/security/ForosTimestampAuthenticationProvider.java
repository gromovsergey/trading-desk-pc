package com.foros.rs.security;

import com.foros.model.security.User;
import com.foros.security.UserAuthenticationValidations;
import com.foros.security.principal.ApplicationPrincipalFactory;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class ForosTimestampAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserAuthenticationValidations userAuthenticationValidations;

    private long timestampImprecision = 30*60*1000;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ForosTimestampAuthenticationToken token = (ForosTimestampAuthenticationToken) authentication;

        WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

        String userToken = (String) token.getPrincipal();
        byte[] signature = (byte[]) token.getCredentials();

        try {
            User user = userAuthenticationValidations
                    .validateTokenWithSignature(userToken, signature, token.getTimestamp(), timestampImprecision);

            return ApplicationPrincipalFactory.createPrincipalByUser(userToken, user, details.getRemoteAddress());
        } catch (ConstraintViolationException e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ForosTimestampAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public long getTimestampImprecision() {
        return timestampImprecision;
    }

    public void setTimestampImprecision(long timestampImprecision) {
        this.timestampImprecision = timestampImprecision;
    }

}
