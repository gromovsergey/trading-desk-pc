package com.foros.web.security;

import com.foros.security.spring.AuthenticationModuleAuthenticationEntryPoint;
import com.foros.security.spring.TokenAuthenticationSupport;
import com.foros.util.customization.CustomizationFilter;
import com.foros.web.filters.CurrentUrlFilter;
import com.foros.web.filters.EnvironmentFilter;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.RequestMatcher;

@Configuration
@TokenAuthenticationSupport
@ImportResource("classpath:ui-security.xml")
public class UiSecurityConfiguration {

    @Bean
    public Filter environmentFilter() {
        return new EnvironmentFilter();
    }

    @Bean
    public Filter currentUrlFilter() {
        return new CurrentUrlFilter();
    }

    @Bean
    public Filter customizationFilter() {
        return new CustomizationFilter();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new UIAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint entryPoint() {
        return new AuthenticationModuleAuthenticationEntryPoint();
    }

    @Bean
    public RequestMatcher errorRequestMatcher() {
        return new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                return request.getDispatcherType() == DispatcherType.ERROR;
            }
        };
    }
}
