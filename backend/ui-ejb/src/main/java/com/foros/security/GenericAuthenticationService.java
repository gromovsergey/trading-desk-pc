package com.foros.security;

import java.util.Set;

import javax.ejb.Local;

@Local
public interface GenericAuthenticationService {

    /**
     * Find user authenticated with this token and return it's authorities
     *
     * @param token authentication token
     *
     * @return set of authorities (user role)
     */
    Set<String> findAuthoritiesByToken(String token);

}
