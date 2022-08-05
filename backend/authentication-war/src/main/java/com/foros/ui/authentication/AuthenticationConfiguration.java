package com.foros.ui.authentication;

import com.foros.security.currentuser.CurrentUserSettingsFilter;
import com.foros.ui.authentication.spring.filter.LocaleFilter;
import com.foros.util.customization.CustomizationFilter;

import javax.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan({"com.foros.ui.authentication"})
@Import(AuthenticationSecurityConfiguration.class)
public class AuthenticationConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public Filter customizationFilter() {
        return new CustomizationFilter();
    }

    @Bean
    public Filter localeFilter() {
        return new LocaleFilter();
    }

    @Bean
    public Filter currentUserSettingsFilter() {
        return new CurrentUserSettingsFilter();
    }
}
