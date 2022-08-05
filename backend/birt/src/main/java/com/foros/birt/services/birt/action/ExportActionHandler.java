package com.foros.birt.services.birt.action;

import com.foros.birt.services.birt.ActionHandlerFactory;
import com.foros.birt.web.util.ExportHelper;
import com.foros.model.report.birt.BirtReportSession;

import javax.servlet.http.HttpServletResponse;
import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.BirtReportServiceFactory;
import org.eclipse.birt.report.service.actionhandler.AbstractBaseActionHandler;
import org.eclipse.birt.report.service.api.IViewerReportService;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("ExportActionHandler")
@Scope("prototype")
public class ExportActionHandler extends AbstractBaseActionHandler {

    @Autowired
    private ActionHandlerFactory actionHandlerFactory;

    private BirtReportSession session;

    public ExportActionHandler(BirtReportSession session, IContext context,
                               Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    @Override
    protected void __execute() throws Exception {
        ExportHelper.prepareExport(context);

        actionHandlerFactory.createRenderActionHandler(session, context, operation, response).execute();
    }

    @Override
    protected IViewerReportService getReportService() {
        return BirtReportServiceFactory.getReportService();
    }
}
