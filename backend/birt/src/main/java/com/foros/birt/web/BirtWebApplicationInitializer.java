package com.foros.birt.web;

import com.foros.birt.config.BirtConfiguration;
import com.foros.birt.web.listener.AuditViewerHttpSessionListener;
import com.foros.birt.web.util.ForwardServlet;
import com.foros.birt.web.util.ServletContextParametersBuilder;
import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.security.spring.WebApplicationInitializerHelper;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.eclipse.birt.report.listener.ViewerServletContextListener;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.eclipse.birt.report.utility.filename.DefaultFilenameGenerator;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class BirtWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = WebApplicationInitializerHelper
                .createSpringContext(servletContext, BirtConfiguration.class);

        WebApplicationInitializerHelper
                .registerSpringSecurityFilterAndListeners(servletContext, context);

        initializeContextParameters(servletContext, context.getBean(Config.class));

        servletContext.addListener(ViewerServletContextListener.class);
        servletContext.addListener(AuditViewerHttpSessionListener.class);

        registerDispatcher(servletContext, context);

        servletContext.addServlet("preview", new ForwardServlet("/reports/preview/")).addMapping("/preview");
    }

    private void registerDispatcher(ServletContext servletContext, WebApplicationContext context) {
        ServletRegistration.Dynamic dispatcher = servletContext
                .addServlet("dispatcher", new DispatcherServlet(context));

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/reports/*");

    }

    private void initializeContextParameters(ServletContext servletContext, Config config) {

        new ServletContextParametersBuilder(servletContext)

                /* Default locale setting */
                .add(ParameterAccessor.INIT_PARAM_LOCALE, "en-US")

                /*
                 Default timezone setting.
		         Examples: "Europe/Paris", "GMT+1".
		         Defaults to the container's timezone.
                 */
                .add(ParameterAccessor.INIT_PARAM_TIMEZONE, "")

                /* Report resources directory for preview. Defaults to ${birt home} */
                .add(ParameterAccessor.INIT_PARAM_WORKING_DIR, "WEB-INF/documents")

                /* Temporary document files directory. Defaults to ${birt home}/documents */
                .add(ParameterAccessor.INIT_PARAM_DOCUMENT_FOLDER, "WEB-INF/documents")

                /*
                 Flag whether the report resources can only be accessed under the
		         working folder. Defaults to true
                 */
                .add(ParameterAccessor.INIT_PARAM_WORKING_FOLDER_ACCESS_ONLY, "")

                /*
                 Settings for how to deal with the url report path. e.g. "http://host/repo/test.rptdesign".

                 Following values are supported:

                 <all> 		- All paths.
                 <domain>	- Only the paths with host matches current domain. Note the comparison is literal,
                              "127.0.0.1" and "localhost" are considered as different hosts.
                 <none> 		- URL paths are not supported.

                 Defaults to "domain".
                 */
                .add(ParameterAccessor.INIT_PARAM_URL_REPORT_PATH_POLICY, "domain")

                /* Temporary image/chart directory. Defaults to ${birt home}/report/images */
                .add(ParameterAccessor.INIT_PARAM_IMAGE_DIR, "")

                /* Engine log directory. Defaults to ${birt home}/logs */
                .add(ParameterAccessor.INIT_PARAM_LOG_DIR, "")

                /* Report engine log level */
                .add(ParameterAccessor.INIT_PARAM_LOG_LEVEL, "SEVERE")

                /*
                Directory where to store all the birt report script libraries (JARs).
		        Defaults to ${birt home}/scriptlib
                */
                .add(ParameterAccessor.INIT_PARAM_SCRIPTLIB_DIR, "")

                /* Resource location directory. Defaults to ${birt home} */
                .add(ParameterAccessor.INIT_PARAM_BIRT_RESOURCE_PATH, "")

                /* Preview report rows limit. An empty value means no limit. */
                .add(ParameterAccessor.INIT_PARAM_VIEWER_MAXROWS,
                        String.valueOf(config.get(ConfigParameters.BIRT_VIEWER_MAX_ROWS)))

                /*
                Max cube fetch levels limit for report preview (Only used when
		        previewing a report design file using the preview pattern)
                */
                .add(ParameterAccessor.INIT_PARAM_VIEWER_MAXCUBE_ROWLEVELS, "")
                .add(ParameterAccessor.INIT_PARAM_VIEWER_MAXCUBE_COLUMNLEVELS, "")

                /* Memory size in MB for creating a cube. */
                .add(ParameterAccessor.INIT_PARAM_VIEWER_CUBEMEMSIZE, "")

                /* Defines the BIRT viewer configuration file */
                .add(ParameterAccessor.INIT_PARAM_CONFIG_FILE, "WEB-INF/viewer.properties")

                /*
                Flag whether to allow server-side printing. Possible values are "ON"
		        and "OFF". Defaults to "ON".
                */
                .add(ParameterAccessor.INIT_PARAM_PRINT_SERVERSIDE, "OFF")

                /* Flag whether to force browser-optimized HTML output. Defaults to true */
                .add(ParameterAccessor.INIT_PARAM_AGENTSTYLE_ENGINE, "true")

                /* Filename generator class/factory to use for the exported reports. */
                .add(ParameterAccessor.INIT_PARAM_FILENAME_GENERATOR_CLASS, DefaultFilenameGenerator.class.getName())

                /* XLS report export report rows limit. Used to enable/disable xls export. */
                .add(ConfigParameters.BIRT_XLS_MAX_ROWS.getName(),
                        String.valueOf(config.get(ConfigParameters.BIRT_XLS_MAX_ROWS)))
        ;
    }

}
