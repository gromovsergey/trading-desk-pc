package com.foros.web;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckSessionFilter implements Filter {

    public static final String CURRENT_SESSION_ATTRIBUTE = "CheckSessionFilter.currentSession";

    private Logger logger = Logger.getLogger(CheckSessionFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        switch (request.getDispatcherType()) {
            case REQUEST:
                request.setAttribute(CURRENT_SESSION_ATTRIBUTE, request.getSession(false));
                break;
            case FORWARD:
            case INCLUDE:
                Object oldSession = request.getAttribute(CURRENT_SESSION_ATTRIBUTE);
                Object newSession = request.getSession(false);
                if (newSession != oldSession && oldSession != null) {
                    logger.warning("Session was changed during request processing!");
                    if (!response.isCommitted()) {
                        response.sendRedirect("/");
                        return;
                    }
                }
                break;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
