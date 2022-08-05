package com.foros.birt.config;

import com.foros.security.spring.AuthenticationModuleAuthenticationEntryPoint;
import com.foros.security.spring.ForosAccessDeniedHandler;
import com.foros.security.spring.TokenAuthenticationSupport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@TokenAuthenticationSupport
@ImportResource("classpath:birt-security.xml")
public class BirtSecurityConfiguration {

    @Bean
    public AuthenticationEntryPoint entryPoint() {
        return new AuthenticationModuleAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new ForosAccessDeniedHandler();
    }
}
