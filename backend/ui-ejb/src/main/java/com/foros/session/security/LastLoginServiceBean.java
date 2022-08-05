package com.foros.session.security;

import com.foros.model.security.AuthenticationToken;
import com.foros.model.security.UserCredential;
import com.foros.security.AuthenticationService;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "LastLoginService")
public class LastLoginServiceBean implements LastLoginService {

    @EJB
    private AuthenticationService authenticationService;

    @Override
    public void update(String token, String ip) {
        AuthenticationToken authenticationToken = authenticationService.findAuthenticationToken(token);

        UserCredential credential = authenticationToken.getUser().getUserCredential();

        credential.setLastLoginDate(new Date());
        credential.setLastLoginIP(ip);
    }
}
