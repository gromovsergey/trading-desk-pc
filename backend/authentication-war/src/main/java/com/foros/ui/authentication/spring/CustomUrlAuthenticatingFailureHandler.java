package com.foros.ui.authentication.spring;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class CustomUrlAuthenticatingFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public CustomUrlAuthenticatingFailureHandler(String url) {
        super(url);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        saveLogin(request);
        super.onAuthenticationFailure(request, response, exception);
    }

    private void saveLogin(HttpServletRequest request) {
        request.getSession(true).setAttribute("wrongUsername", request.getParameter("j_username"));
    }
}
