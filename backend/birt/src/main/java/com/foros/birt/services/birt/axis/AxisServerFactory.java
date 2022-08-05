package com.foros.birt.services.birt.axis;

import com.foros.birt.services.birt.ActionHandlerFactory;
import com.foros.model.report.birt.BirtReportSession;

import java.io.File;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.server.AxisServer;
import org.eclipse.birt.report.context.BirtContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AxisServerFactory {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ActionHandlerFactory actionHandlerFactory;

    public AxisServer create(BirtReportSession session, BirtContext context) {
        WSDDContextProvider provider = new WSDDContextProvider(new InvokerImpl(actionHandlerFactory, session, context));

        WSDDProvider.registerProvider(new QName(WSDDConstants.URI_WSDD_JAVA, provider.getName()), provider);

        String realPath = servletContext.getRealPath("WEB-INF/");

        return new AxisServer(new FileProvider(realPath + File.separator + "server-config.wsdd"));
    }

}
