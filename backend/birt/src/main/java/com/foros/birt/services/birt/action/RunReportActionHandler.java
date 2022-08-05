package com.foros.birt.services.birt.action;

import com.foros.birt.services.birt.BirtReportHelperService;
import com.foros.birt.utils.BirtUtils;
import com.foros.model.report.birt.BirtReportInstance;
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

@Component("RunReportActionHandler")
@Scope("prototype")
public class RunReportActionHandler extends AbstractBaseActionHandler {

    @Autowired
    private BirtReportService birtReportService;

    @Autowired
    private BirtReportHelperService birtReportHelperService;

    private BirtReportSession session;

    public RunReportActionHandler(BirtReportSession session, IContext context,
                                  Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    public void __execute() throws Exception {
        BirtReportHelperService.RunReportParameters runReportParameters = BirtUtils.fetchRunReportParameters(context, operation);

        BirtReportInstance instance = birtReportHelperService.runReport(session, runReportParameters);

        context.getBean().setReportDocumentName(birtReportService.getReportDocumentAbsoluteFileName(instance));
    }

    protected IViewerReportService getReportService() {
        return BirtReportServiceFactory.getReportService();
    }
}
