package com.foros.security.spring.provider.validations;

import com.foros.model.security.AuthenticationToken;
import com.foros.security.AuthenticationService;
import com.foros.security.principal.ApplicationPrincipal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

public class TokenExistsPrincipalValidation implements PrincipalValidation {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean isValid(HttpServletRequest request, ApplicationPrincipal authentication) {
        String token = authentication.getToken();
        AuthenticationToken authenticationToken = authenticationService.findAuthenticationToken(token);

        return authenticationToken != null;
    }
}
