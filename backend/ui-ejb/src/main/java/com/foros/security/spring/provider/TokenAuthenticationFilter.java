package com.foros.security.spring.provider;

import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.Tokenable;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

public class TokenAuthenticationFilter extends GenericFilterBean implements ApplicationEventPublisherAware {

    private AuthenticationManager authenticationManager;
    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private ApplicationEventPublisher applicationEventPublisher;

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
        }
    }

    private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = TokenUtils.fetchTokenFromRequest(request);

        if (requiresAuthentication(token)) {
            doAuthentication(token, request, response);
        }

        chain.doFilter(request, response);
    }

    private void doAuthentication(String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(token, null);

            authentication.setDetails(authenticationDetailsSource.buildDetails(request));

            Authentication result = authenticationManager.authenticate(authentication);

            if (result != null && !(result instanceof Tokenable)) {
                throw new AuthenticationServiceException(result + " not supported");
            }

            if (result != null && result.isAuthenticated()) {
                request.getSession().invalidate();

                successAuthentication(result);
                return;
            }
        } catch (AuthenticationException e) {
            logger.info("Authentication with token '" + token+ "' failed: " + e.getMessage());
            // do nothing
        }

        TokenUtils.removeToken(request, response);

        unsuccessfulAuthentication();
    }

    private void unsuccessfulAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private void successAuthentication(Authentication result) {
        SecurityContextHolder.getContext().setAuthentication(result);

        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(result, this.getClass()));
        }
    }

    protected boolean requiresAuthentication(String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (token == null || token.isEmpty()) {
            return true;
        }

        if (!(authentication instanceof ApplicationPrincipal)) {
            return true;
        }

        ApplicationPrincipal principal = (ApplicationPrincipal) authentication;

        return !principal.isAuthenticated() || !principal.getToken().equals(token);
    }

}
