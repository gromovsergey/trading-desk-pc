package app.programmatic.ui.common.config;

import app.programmatic.ui.angular.AngularHelper;
import app.programmatic.ui.angular.AngularServlet;
import app.programmatic.ui.common.interceptor.AuthorizationInterceptor;
import app.programmatic.ui.common.interceptor.NoCacheInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;


@EnableConfigurationProperties({ WebMvcConfig.WebSettings.class })
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    @Autowired
    private NoCacheInterceptor noCacheInterceptor;

    @Bean
    public AuthorizationInterceptor authorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Bean
    public NoCacheInterceptor noCacheInterceptor() {
        return new NoCacheInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor);
        registry.addInterceptor(noCacheInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<String> resourceLocations = new ArrayList<>();

        AngularHelper.ContentLocation devStaticLocation = AngularHelper.getDevAngularFilesLocation();
        if (devStaticLocation.exists()) {
            resourceLocations.add(devStaticLocation.getPath());
        }

        resourceLocations.add("classpath:/META-INF/resources/");
        resourceLocations.add("classpath:/resources/");
        resourceLocations.add("classpath:/static/");
        resourceLocations.add("classpath:/public/");

        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    resourceLocations.toArray(new String[resourceLocations.size()]));
        }
    }

    @Bean
    public ServletRegistrationBean angularServletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new AngularServlet(),
                "",
                "/index.html",
                "/advertiser/*",
                "/agency/*",
                "/agentreport/*",
                "/audienceresearch/*",
                "/conversion/*",
                "/channel/*",
                "/dashboard/*",
                "/flight/*",
                "/lineitem/*",
                "/login/*",
                "/logout/*",
                "/my/*",
                "/report/*",
                "/user/*"
        );
        registration.setLoadOnStartup(0);
        return registration;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(WebSettings webSettings) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (!webSettings.getEnableCrossOrigin()) {
                    return;
                }

                registry.addMapping("/**")
                        .allowedOrigins(webSettings.getCrossOriginDomains().toArray(
                                            new String[webSettings.getCrossOriginDomains().size()]))
                        .allowedMethods(HttpMethod.GET.name(),
                                        HttpMethod.HEAD.name(),
                                        HttpMethod.POST.name(),
                                        HttpMethod.PUT.name(),
                                        HttpMethod.DELETE.name());
            }
        };
    }

    @ConfigurationProperties("web")
    public static class WebSettings {
        private Boolean useProdMode;
        private String restBaseUrl;
        private Boolean enableCrossOrigin;
        private String staticResourceUrl;
        private List<String> crossOriginDomains;

        public String getRestBaseUrl() {
            return restBaseUrl;
        }

        public void setRestBaseUrl(String restBaseUrl) {
            this.restBaseUrl = restBaseUrl;
        }

        public Boolean getEnableCrossOrigin() {
            return enableCrossOrigin;
        }

        public void setEnableCrossOrigin(Boolean enableCrossOrigin) {
            this.enableCrossOrigin = enableCrossOrigin;
        }

        public List<String> getCrossOriginDomains() {
            return crossOriginDomains;
        }

        public void setCrossOriginDomains(List<String> crossOriginDomains) {
            this.crossOriginDomains = crossOriginDomains;
        }

        public Boolean getUseProdMode() {
            return useProdMode;
        }

        public void setUseProdMode(Boolean useProdMode) {
            this.useProdMode = useProdMode;
        }
    }
}
