package com.foros.ui.authentication;

import com.foros.model.VersionHelper;
import com.foros.security.spring.WebApplicationInitializerHelper;
import com.foros.ui.authentication.spring.filter.LocaleFilter;
import com.foros.util.customization.CustomizationFilter;
import com.foros.util.web.NoCacheFilter;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class AuthenticationWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        FilterRegistration.Dynamic characterEncoding = servletContext.addFilter("characterEncoding", characterEncodingFilter);
        characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

        WebApplicationContext context = WebApplicationInitializerHelper
                .createSpringContext(servletContext, AuthenticationConfiguration.class);

        WebApplicationInitializerHelper
                .registerSpringSecurityFilterAndListeners(servletContext, context);

        servletContext
                .addFilter("nocache", new NoCacheFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

        servletContext
                .addFilter("locale", context.getBean(LocaleFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        servletContext
                .addFilter("customization", context.getBean(CustomizationFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        servletContext.addServlet("dispatcher", new DispatcherServlet(context)).addMapping("/");

        servletContext.setInitParameter("javax.servlet.jsp.jstl.fmt.localizationContext", "resource.applicationResource");
        servletContext.setAttribute("timestampVersion", VersionHelper.getBuildTimestamp());
    }
}
