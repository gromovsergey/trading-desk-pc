package com.foros.birt.services.birt.action;

import com.foros.birt.services.birt.BirtReportHelperService;
import com.foros.birt.utils.BirtUtils;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.BirtReportServiceFactory;
import org.eclipse.birt.report.service.actionhandler.AbstractBaseActionHandler;
import org.eclipse.birt.report.service.api.IViewerReportService;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("RenderReportActionHandler")
@Scope("prototype")
public class RenderReportActionHandler extends AbstractBaseActionHandler {

    @Autowired
    private BirtReportService birtReportService;

    @Autowired
    private BirtReportHelperService birtReportHelperService;

    private BirtReportSession session;

    public RenderReportActionHandler(BirtReportSession session, IContext context,
                                     Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    public void __execute() throws Exception {
        BirtReportHelperService.RenderReportParameters renderReportParameters = BirtUtils.fetchRenderReportParameters(context, operation);

        birtReportHelperService.renderReport(session.getBirtReportInstance(), renderReportParameters, context.getResponse().getOutputStream());
    }

    protected IViewerReportService getReportService() {
        return BirtReportServiceFactory.getReportService();
    }
}
