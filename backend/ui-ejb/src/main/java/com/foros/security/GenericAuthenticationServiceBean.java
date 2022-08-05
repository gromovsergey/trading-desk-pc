package com.foros.security;

import com.foros.model.security.AuthenticationToken;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "GenericAuthenticationService")
public class GenericAuthenticationServiceBean implements GenericAuthenticationService {

    private static final Logger logger = Logger.getLogger(GenericAuthenticationServiceBean.class.getName());

    @EJB
    private AuthenticationService authenticationService;

    @Override
    public Set<String> findAuthoritiesByToken(String token) {
        try {
            AuthenticationToken authenticationToken = authenticationService.findAuthenticationToken(token);

            if (authenticationToken != null) {
                return Collections.singleton(authenticationToken.getUser().getAccount().getRole().getName());
            } else {
                return Collections.emptySet();
            }
        } catch (Exception e) {
            logger.throwing("AuthenticationServiceBean", "findAuthoritiesByToken", e);

            throw new RuntimeException(e.getMessage());
        }
    }

}
