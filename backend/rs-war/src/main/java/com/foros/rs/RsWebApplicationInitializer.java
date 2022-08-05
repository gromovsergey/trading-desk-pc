package com.foros.rs;

import com.foros.rs.security.CheckRestrictionsFilter;
import com.foros.security.spring.WebApplicationInitializerHelper;
import com.foros.util.customization.CustomizationFilter;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

public class RsWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = WebApplicationInitializerHelper.createSpringContext(servletContext, RsSecurityConfiguration.class);
        WebApplicationInitializerHelper.registerSpringSecurityFilterAndListeners(servletContext, context);

        servletContext.addFilter("checkPermissionFilter", CheckRestrictionsFilter.class)
            .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        servletContext.addFilter("customization", context.getBean(CustomizationFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        ServletRegistration.Dynamic jerseyServlet = servletContext.addServlet("jerseyServlet", ServletContainer.class);
        jerseyServlet.setInitParameter("com.sun.jersey.config.property.packages", "com.foros.rs");
        jerseyServlet.addMapping("/*");

    }

}
