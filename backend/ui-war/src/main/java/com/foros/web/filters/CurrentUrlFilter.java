package com.foros.web.filters;

import com.foros.util.StringUtil;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class CurrentUrlFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.setAttribute("_url", getUrl(request));
        request.setAttribute("_method", request.getMethod());
        filterChain.doFilter(request, response);
    }

    private String getUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        StringBuilder builder = new StringBuilder(url);

        String queryString = request.getQueryString();
        if (StringUtil.isPropertyNotEmpty(queryString)) {
            builder.append("?").append(queryString);
        }

        return builder.toString();
    }
}
