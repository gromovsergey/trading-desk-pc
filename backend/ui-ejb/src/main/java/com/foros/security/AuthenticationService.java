package com.foros.security;

import com.foros.model.security.AuthenticationToken;

import javax.ejb.Local;

@Local
public interface AuthenticationService {


    /**
     *
     * @param token authentication token
     * @return token or null if token not found
     **/
    AuthenticationToken findAuthenticationToken(String token);

    /**
     *
     * @param username
     * @param password
     * @param priorityUserId
     *
     * @param remoteAddress
     * @return token associated with user
     */
    String login(String username, String password, Long priorityUserId, String remoteAddress);

    /**
     * @param token
     * @param userIdToSwitch
     *
     * @return token associated with swithced user
     */
    String switchUser(String token, Long userIdToSwitch, String remoteAddress);

    /**
     * Remove token associated with user
     *
     * @param token
     */
    void removeToken(String token);

}
