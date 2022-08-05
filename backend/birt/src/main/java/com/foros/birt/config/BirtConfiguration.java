package com.foros.birt.config;

import com.foros.birt.web.util.ExceptionUtils;
import com.foros.birt.web.util.ModelBuilder;
import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.restriction.RestrictionService;
import com.foros.session.birt.BirtReportService;
import com.foros.session.birt.BirtReportService;
import com.foros.session.security.AuditService;

import javax.naming.NamingException;
import com.foros.session.spring.ImportEjb;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ImportEjb
@EnableWebMvc
@ComponentScan({"com.foros.birt.services", "com.foros.birt.web"})
@Import({BirtSecurityConfiguration.class, FragmentsConfiguration.class})
public class BirtConfiguration {

    @Bean
    public HandlerExceptionResolver exceptionResolver() {
        return new AbstractHandlerExceptionResolver() {

            @Override
            protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                logger.error("Report processing failed", ex);

                response.setStatus(ExceptionUtils.getResponseStatusByException(ex));

                return new ModelAndView("common/Error",
                        ModelBuilder.create("error", ex).build());
            }
        };
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/webcontent/birt/pages/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

}
