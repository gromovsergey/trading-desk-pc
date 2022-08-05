package com.foros.ui.authentication.spring.handler;

import com.foros.security.AuthenticationService;
import com.foros.security.spring.provider.TokenUtils;
import com.foros.ui.authentication.spring.TokenAuthentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class RemoveTokenLogoutHandler implements LogoutHandler {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = TokenUtils.fetchTokenFromRequest(request);
        if (token != null) {
            authenticationService.removeToken(token);
            TokenUtils.removeToken(request, response);
        }
    }
}
