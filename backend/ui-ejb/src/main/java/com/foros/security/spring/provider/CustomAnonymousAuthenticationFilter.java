package com.foros.security.spring.provider;

import com.foros.security.principal.ApplicationPrincipalFactory;
import com.foros.security.principal.SecurityContext;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class CustomAnonymousAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (SecurityContext.getPrincipal() == null) {
            SecurityContext.setPrincipal(ApplicationPrincipalFactory.createAnonymousPrincipal(servletRequest.getRemoteAddr()));
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
