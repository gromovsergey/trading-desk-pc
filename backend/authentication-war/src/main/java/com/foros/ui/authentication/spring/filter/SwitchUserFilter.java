package com.foros.ui.authentication.spring.filter;

import com.foros.security.principal.Tokenable;
import com.foros.security.spring.LastSwitchedUserHelper;
import com.foros.security.spring.provider.TokenUtils;
import com.foros.ui.authentication.spring.AdvancedAuthenticationDetailsSource;
import com.foros.ui.authentication.spring.SwitchUserAuthenticationToken;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SwitchUserFilter extends org.springframework.security.web.authentication.switchuser.SwitchUserFilter {
    private AdvancedAuthenticationDetailsSource authenticationDetailsSource;
    private AuthenticationManager authenticationManager;

    public SwitchUserFilter(AuthenticationManager authenticationManager, AdvancedAuthenticationDetailsSource authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!requiresSwitchUser(request)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = Long.valueOf(request.getParameter("j_switch_to_user_id"));
        SwitchUserAuthenticationToken switchUserAuthenticationToken = new SwitchUserAuthenticationToken(userId);
        switchUserAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));

        Authentication authentication = authenticationManager.authenticate(switchUserAuthenticationToken);

        TokenUtils.saveToken(request, response, (Tokenable) authentication);

        LastSwitchedUserHelper.saveLastSwitchedUser(response, userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.getRequestDispatcher("/j_authenticate").forward(request, response);
    }

    protected boolean requiresSwitchUser(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith(request.getContextPath() + "/j_spring_switch_user");
    }
}
