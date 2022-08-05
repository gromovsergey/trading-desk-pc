package com.foros.rs.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

public class ForosTimestampAuthenticationFilter extends OncePerRequestFilter {

    private WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();

    private AuthenticationManager authenticationManager;

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public WebAuthenticationDetailsSource getDetailsSource() {
        return detailsSource;
    }

    public void setDetailsSource(WebAuthenticationDetailsSource detailsSource) {
        this.detailsSource = detailsSource;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = attemptAuthentication(request, response);

            if (authentication.isAuthenticated()) {
                successfulAuthentication(authentication);
            } else {
                unsuccessfulAuthentication();
            }
        } catch (AuthenticationException e) {
            unsuccessfulAuthentication();
        }

        filterChain.doFilter(request, response);
    }

    private void unsuccessfulAuthentication() throws IOException {
        SecurityContextHolder.clearContext();
    }

    private void successfulAuthentication(Authentication authentication) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Token token = getToken(request);

        if (token == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication not found");
        }

        if (!token.getAuthType().equals(Constants.FOROS_TIMESTAMP_AUTH_TYPE)) {
            throw new BadCredentialsException("Authentication type " + token.getAuthType() + " not supported.");
        }

        String timestampHeader = request.getHeader(Constants.TIMESTAMP_HEADER);

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (NumberFormatException e) {
            throw new AuthenticationServiceException("Authentication failed", e);
        }

        ForosTimestampAuthenticationToken authentication = new ForosTimestampAuthenticationToken(
                token.getToken(), token.getSignature(), timestamp);

        authentication.setDetails(detailsSource.buildDetails(request));

        return getAuthenticationManager().authenticate(authentication);
    }

    private Token getToken(HttpServletRequest request) {
        try {
            String authorization = request.getHeader(Constants.AUTHORIZATION_HEADER);
            int space = authorization.indexOf(" ");
            String authType = authorization.substring(0, space);
            String authToken = authorization.substring(space + 1);
            int colon = authToken.indexOf(":");
            String userId = authToken.substring(0, colon);
            String signature = authToken.substring(colon + 1);
            return new Token(authType, userId, DatatypeConverter.parseBase64Binary(signature));
        } catch (Exception e) {
            return null;
        }
    }

    private static final class Token {
        private String authType;
        private String token;
        private byte[] signature;

        private Token(String authType, String token, byte[] signature) {
            this.authType = authType;
            this.token = token;
            this.signature = signature;
        }

        public String getAuthType() {
            return authType;
        }

        public String getToken() {
            return token;
        }

        public byte[] getSignature() {
            return signature;
        }
    }

    private static final class Constants {
        private static final String FOROS_TIMESTAMP_AUTH_TYPE = "TIMESTAMP";

        private static final String AUTHORIZATION_HEADER = "Authorization";
        private static final String TIMESTAMP_HEADER = "X-Timestamp";
    }
}
