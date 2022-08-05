package com.foros.rs;

import com.foros.rs.security.ForosTimestampAuthenticationFilter;
import com.foros.rs.security.ForosTimestampAuthenticationProvider;
import com.foros.security.currentuser.CurrentUserSettingsFilter;
import com.foros.session.spring.ImportEjb;
import com.foros.util.customization.CustomizationFilter;

import java.util.Arrays;
import javax.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@ImportEjb
@ImportResource("classpath:rs-security.xml")
public class RsSecurityConfiguration {

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new Http403ForbiddenEntryPoint();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(
                Arrays.<AuthenticationProvider>asList(
                        forosTimestampAuthenticationProvider()
                )
        );
    }

    @Bean
    public ForosTimestampAuthenticationProvider forosTimestampAuthenticationProvider() {
        return new ForosTimestampAuthenticationProvider();
    }

    @Bean
    public Filter forosTimestampAuthenticationFilter() {
        ForosTimestampAuthenticationFilter filter = new ForosTimestampAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public Filter currentUserSettingsFilter() {
        return new CurrentUserSettingsFilter();
    }

    @Bean
    public Filter customizationFilter() {
        return new CustomizationFilter();
    }
}
