package com.foros.security.spring;

import com.foros.security.currentuser.CurrentUserSettingsFilter;
import com.foros.security.spring.provider.CustomAnonymousAuthenticationFilter;
import com.foros.security.spring.provider.PrincipalValidationAuthenticationFilter;
import com.foros.security.spring.provider.TokenAuthenticationFilter;
import com.foros.security.spring.provider.TokenAuthenticationProvider;
import com.foros.security.spring.provider.validations.PrincipalValidation;
import com.foros.security.spring.provider.validations.PrincipalValidations;
import com.foros.security.spring.provider.validations.StatusValidation;
import com.foros.security.spring.provider.validations.TokenExistsPrincipalValidation;
import com.foros.security.spring.provider.validations.UserRoleValidation;
import com.foros.session.spring.ImportEjb;

import java.util.Arrays;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@ImportEjb
public class TokenAuthenticationConfiguration {

    @Bean
    public Filter currentUserSettingsFilter() {
        return new CurrentUserSettingsFilter();
    }

    @Bean
    public Filter principalValidationAuthenticationFilter() {
        return new PrincipalValidationAuthenticationFilter(
                PrincipalValidations.constantIpValidation(),
                tokenExistsPrincipalValidation(),
                userRoleValidation(),
                statusValidation()
        );
    }

    @Bean
    public PrincipalValidation statusValidation() {
        return new StatusValidation();
    }

    @Bean
    public PrincipalValidation userRoleValidation() {
        return new UserRoleValidation();
    }

    @Bean
    public PrincipalValidation tokenExistsPrincipalValidation() {
        return new TokenExistsPrincipalValidation();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public Filter tokenAuthenticationFilter() {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationDetailsSource(new WebAuthenticationDetailsSource());
        return filter;
    }

    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.<AuthenticationProvider>asList(
                tokenAuthenticationProvider()
        ));
    }

    @Bean
    public Filter anonymousAuthenticationFilter() {
        return new CustomAnonymousAuthenticationFilter();
    }

}
