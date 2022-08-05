package com.foros.ui.authentication.spring.filter;

import com.foros.security.spring.provider.TokenUtils;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class TokenAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return TokenUtils.fetchTokenFromRequest(request);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }
}
