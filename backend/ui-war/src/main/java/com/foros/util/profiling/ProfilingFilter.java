package com.foros.util.profiling;

import com.foros.profiling.request.RequestHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ProfilingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            RequestHolder.set((HttpServletRequest) request);
            try {
                chain.doFilter(request, response);
            } finally {
                RequestHolder.set(null);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
