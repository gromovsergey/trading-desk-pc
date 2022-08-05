package com.foros.birt.services.birt.action;

import com.foros.birt.services.birt.ActionHandlerFactory;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import java.rmi.RemoteException;
import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.BirtReportServiceFactory;
import org.eclipse.birt.report.service.actionhandler.AbstractGetPageActionHandler;
import org.eclipse.birt.report.service.api.IViewerReportService;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("GetPageActionHandler")
@Scope("prototype")
public class GetPageActionHandler extends AbstractGetPageActionHandler {

    @Autowired
    private BirtReportService birtReportService;

    @Autowired
    private ActionHandlerFactory actionHandlerFactory;

    private BirtReportSession session;

    public GetPageActionHandler(BirtReportSession session, IContext context,
                                Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    @Override
    protected String __getReportDocument() {
        if (session.getBirtReportInstance() == null) {
            try {
                actionHandlerFactory.createRunReportActionHandler(session, context, operation, response).execute();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        return birtReportService.getReportDocumentAbsoluteFileName(session.getBirtReportInstance());
    }

    @Override
    protected void __checkDocumentExists() throws Exception {
    }

    @Override
    protected IViewerReportService getReportService() {
        return BirtReportServiceFactory.getReportService();
    }
}
