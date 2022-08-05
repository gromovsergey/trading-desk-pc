package com.foros.security.spring.provider;

import com.foros.security.AuthenticationService;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.spring.provider.validations.PrincipalValidation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class PrincipalValidationAuthenticationFilter extends OncePerRequestFilter {

    private List<PrincipalValidation> principalValidations;

    @EJB
    private AuthenticationService authenticationService;

    public PrincipalValidationAuthenticationFilter(PrincipalValidation... principalValidations) {
        this.principalValidations = Arrays.asList(principalValidations);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof ApplicationPrincipal) {
            ApplicationPrincipal principal = (ApplicationPrincipal) authentication;
            if (!principal.isAnonymous() && !validate(request, principal)) {
                authentication.setAuthenticated(false);
                authenticationService.removeToken(principal.getToken());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean validate(HttpServletRequest request, ApplicationPrincipal principal) {
        for (PrincipalValidation principalValidation : principalValidations) {
            if (!principalValidation.isValid(request, principal)) {
                return false;
            }
        }

        return true;
    }

}
