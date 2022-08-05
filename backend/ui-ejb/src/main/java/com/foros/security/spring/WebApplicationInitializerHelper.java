package com.foros.security.spring;

import com.foros.security.currentuser.CurrentUserSettingsFilter;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

public abstract class WebApplicationInitializerHelper {

    public static void registerSpringSecurityFilterAndListeners(ServletContext servletContext, WebApplicationContext context) {
        servletContext.addListener(new ContextLoaderListener(context));

        servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR), true, "/*");

        if (!context.getBeansOfType(CurrentUserSettingsFilter.class).isEmpty()) {
            servletContext.addFilter("currentUserSettingsFilter", context.getBean(CurrentUserSettingsFilter.class))
                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), true, "/*");
        }
    }

    public static WebApplicationContext createSpringContext(ServletContext servletContext, Class<?> configurationClass) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.register(configurationClass);

        context.refresh();

        return context;
    }
}
