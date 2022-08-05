package com.foros.rs.security;

import com.foros.session.ServiceLocator;
import com.foros.session.ServiceLookup;
import com.foros.session.security.APIRestrictions;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckRestrictionsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ServiceLookup serviceLookup = ServiceLocator.getInstance();

        APIRestrictions apiRestrictions = serviceLookup.lookup(APIRestrictions.class);

        if (!apiRestrictions.canRun()) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
