package com.foros.ui.authentication;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

public class ReturnUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String returnUrl = request.getParameter("j_return_url");
        String uri = UriComponentsBuilder.fromPath("/j_authenticate")
                .queryParam("j_return_url", returnUrl)
                .build()
                .toUriString();
        redirectStrategy.sendRedirect(request, response, uri);
    }
}
