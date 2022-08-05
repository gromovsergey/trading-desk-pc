package com.foros.security.spring;

import com.foros.security.spring.utils.RedirectStrategies;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.UriComponentsBuilder;

public class AuthenticationModuleAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private RedirectStrategy redirectStrategy = RedirectStrategies.createRelativeStrategy();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String returnUrl = null;
        if ("GET".equals(request.getMethod())) {
            returnUrl = UriComponentsBuilder.fromPath(request.getRequestURI())
                    .query(request.getQueryString())
                    .build().toUriString();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String path;
        if (authentication != null && authentication.isAuthenticated()) {
            path = "/login/j_spring_security_logout";
        } else {
            path = "/login/j_authenticate";
        }

        String url = UriComponentsBuilder.fromPath(path)
                .queryParam("j_return_url", returnUrl)
                .build()
                .encode()
                .toUriString();

        redirectStrategy.sendRedirect(request, response, url);
    }
}