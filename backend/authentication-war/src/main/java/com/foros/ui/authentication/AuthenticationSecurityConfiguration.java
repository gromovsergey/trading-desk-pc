package com.foros.ui.authentication;

import com.foros.security.currentuser.CurrentUserSettingsFilter;
import com.foros.security.principal.Tokenable;
import com.foros.security.spring.provider.TokenAuthenticationProvider;
import com.foros.security.spring.provider.TokenUtils;
import com.foros.session.spring.ImportEjb;
import com.foros.ui.authentication.spring.AdvancedAuthenticationDetailsSource;
import com.foros.ui.authentication.spring.CustomUrlAuthenticatingFailureHandler;
import com.foros.ui.authentication.spring.SwitchUserAuthenticationProvider;
import com.foros.ui.authentication.spring.UserPasswordAuthenticationProvider;
import com.foros.ui.authentication.spring.filter.SwitchUserFilter;
import com.foros.ui.authentication.spring.filter.TokenAuthenticatedProcessingFilter;
import com.foros.ui.authentication.spring.handler.RemoveTokenLogoutHandler;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.NullSecurityContextRepository;

@Configuration
@ImportEjb
@EnableWebSecurity
public class AuthenticationSecurityConfiguration  extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PAGE = "/login";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernamePasswordAuthenticationProvider());
        auth.authenticationProvider(switchUserAuthenticationProvider());
        auth.authenticationProvider(tokenAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**");
        http.securityContext()
                .securityContextRepository(new NullSecurityContextRepository());

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

        http.formLogin()
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage(LOGIN_PAGE)
                .usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                .passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
                .successHandler(usernamePasswordAuthenticationSuccessHandler())
                .failureHandler(usernamePasswordAuthenticationFailureHandler())
                .authenticationDetailsSource(advancedAuthenticationDetailsSource());

        http.anonymous().disable();

        http.csrf().disable();

        http.logout()
                .logoutUrl("/j_spring_security_logout")
                .addLogoutHandler(removeTokenLogoutHandler())
                .logoutSuccessHandler(returnUrlLogoutSuccessHandler());

        AuthenticationManager authenticationManager = authenticationManager();
        http.addFilter(tokenAuthenticatedProcessingFilter(authenticationManager));
        http.addFilter(switchUserFilter(authenticationManager));

        http.authorizeRequests().antMatchers("/j_authenticate","/j_spring_switch_user").not().anonymous();
    }

    @Bean
    public AuthenticationProvider usernamePasswordAuthenticationProvider() {
        return new UserPasswordAuthenticationProvider();
    }

    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider();
    }

    @Bean
    public AuthenticationProvider switchUserAuthenticationProvider() {
        return new SwitchUserAuthenticationProvider();
    }

    @Bean
    public AuthenticationFailureHandler usernamePasswordAuthenticationFailureHandler() {
        return new CustomUrlAuthenticatingFailureHandler(LOGIN_PAGE);
    }

    @Bean
    public LogoutHandler removeTokenLogoutHandler() {
        return new RemoveTokenLogoutHandler();
    }

    @Bean
    public AdvancedAuthenticationDetailsSource advancedAuthenticationDetailsSource() {
        return new AdvancedAuthenticationDetailsSource();
    }

    @Bean
    public Filter currentUserSettingsFilter() {
        return new CurrentUserSettingsFilter();
    }

    @Bean
    public AuthenticationSuccessHandler usernamePasswordAuthenticationSuccessHandler() {
        final AuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                TokenUtils.saveToken(request, response, (Tokenable) authentication);
                savedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher() {
        return new DefaultAuthenticationEventPublisher();
    }

    @Bean
    public AccessDeniedHandler returnAccessDeniedHandler() {
        return new ReturnAccessDeniedHandler();
    }

    @Bean
    public LogoutSuccessHandler returnUrlLogoutSuccessHandler() {
        return new ReturnUrlLogoutSuccessHandler();
    }

    private TokenAuthenticatedProcessingFilter tokenAuthenticatedProcessingFilter(AuthenticationManager authenticationManager) {
        TokenAuthenticatedProcessingFilter filter = new TokenAuthenticatedProcessingFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationDetailsSource(new AdvancedAuthenticationDetailsSource());
        return filter;
    }

    private Filter switchUserFilter(AuthenticationManager authenticationManager) {
        return new SwitchUserFilter(authenticationManager, advancedAuthenticationDetailsSource());
    }
}
