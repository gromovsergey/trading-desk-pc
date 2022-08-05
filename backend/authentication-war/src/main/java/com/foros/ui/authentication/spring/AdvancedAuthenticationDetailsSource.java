package com.foros.ui.authentication.spring;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class AdvancedAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, AdvancedWebAuthenticationDetails> {

    @Override
    public AdvancedWebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new AdvancedWebAuthenticationDetails(context);
    }

}
