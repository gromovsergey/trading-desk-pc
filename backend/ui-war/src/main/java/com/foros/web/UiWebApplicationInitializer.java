package com.foros.web;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.VersionHelper;
import com.foros.security.spring.WebApplicationInitializerHelper;
import com.foros.util.customization.CustomizationFilter;
import com.foros.web.filters.CurrentUrlFilter;
import com.foros.web.filters.EnvironmentFilter;
import com.foros.web.resources.AssetFactoryImpl;
import com.foros.web.resources.AssetManager;
import com.foros.web.resources.CachedAssetManagerWrapper;
import com.foros.web.resources.ResourcesServlet;
import com.foros.web.resources.SimpleAssetManager;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;

public class UiWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = WebApplicationInitializerHelper
                .createSpringContext(servletContext, UiConfiguration.class);

        servletContext.addFilter("currentUrl", context.getBean(CurrentUrlFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.action", "*.jsp");

        WebApplicationInitializerHelper.registerSpringSecurityFilterAndListeners(servletContext, context);

        servletContext.addFilter("environment", context.getBean(EnvironmentFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), true, "*.action", "*.jsp");

        servletContext.addFilter("customization", context.getBean(CustomizationFilter.class))
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), true, "/*");

        ConfigService configService = context.getBean(ConfigService.class);
        servletContext.addServlet("assets", new ResourcesServlet(createAssetManager(configService, servletContext)))
                .addMapping("/scripts/*", "/styles/*");

        registerStrutsFilter(servletContext);
    }

    private AssetManager createAssetManager(Config config, ServletContext servletContext) {

        long version = Long.parseLong(VersionHelper.getBuildTimestamp());

        AssetManager assetManager = createAssetManagerByMode(version, config.get(ConfigParameters.DEVELOPMENT_MODE));

        assetManager.add("scripts", new AssetFactoryImpl(servletContext, "/js", "text/javascript"));
        assetManager.add("styles", new AssetFactoryImpl(servletContext, "/css", "text/css"));

        return assetManager;
    }

    private AssetManager createAssetManagerByMode(long version, boolean developmentMode) {
        if (developmentMode) {
            return new SimpleAssetManager(version);
        } else {
            return new CachedAssetManagerWrapper(new SimpleAssetManager(version));
        }
    }

    private void registerStrutsFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic struts2Filter = servletContext.addFilter("struts2Filter", StrutsPrepareAndExecuteFilter.class);
        struts2Filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE, DispatcherType.FORWARD), true, "*.action");
        struts2Filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.INCLUDE), true, "*.js");
        struts2Filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.ERROR), true, "/*" );
    }

}
