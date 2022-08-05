package com.foros.ui.authentication;

import com.foros.security.spring.utils.RedirectStrategies;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;

public class ReturnAccessDeniedHandler implements AccessDeniedHandler {

    private RedirectStrategy redirectStrategy = RedirectStrategies.createRelativeStrategy();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        redirectStrategy.sendRedirect(request, response, "/");
    }
}
