package com.foros.web.filters;

import com.foros.util.web.ResponseCacheHelper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CachingFilter implements Filter {

    /**
     * expiration time in the future to use for the caching directives
     */
    private long expirationTime = 31536000000l; // one year

    @Override
    public void init(FilterConfig config) throws ServletException {
        String expirationTimeParameter = config.getInitParameter("expiration-time");
        if (expirationTimeParameter != null) {
            this.expirationTime = Long.parseLong(expirationTimeParameter);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            ResponseCacheHelper.setCached(httpServletResponse, expirationTime);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
