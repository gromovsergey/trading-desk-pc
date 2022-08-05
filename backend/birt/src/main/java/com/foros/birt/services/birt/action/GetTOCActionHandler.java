package com.foros.birt.services.birt.action;

import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import javax.servlet.http.HttpServletResponse;
import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.BirtReportServiceFactory;
import org.eclipse.birt.report.service.actionhandler.AbstractBaseActionHandler;
import org.eclipse.birt.report.service.actionhandler.AbstractGetTOCActionHandler;
import org.eclipse.birt.report.service.api.IViewerReportService;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("GetTocActionHandler")
@Scope("prototype")
public class GetTOCActionHandler extends AbstractGetTOCActionHandler {

    @Autowired
    private BirtReportService reportService;

    private BirtReportSession session;

    public GetTOCActionHandler(BirtReportSession session, IContext context,
                               Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    @Override
    protected String __getReportDocument() {
        return reportService.getReportDocumentAbsoluteFileName(session.getBirtReportInstance());
    }

    @Override
    protected void __checkDocumentExists() throws Exception {
        if (session.getBirtReportInstance() == null) {
            throw new IllegalStateException("You need to run report before got a TOC.");
        }
    }

    @Override
    protected IViewerReportService getReportService() {
        return BirtReportServiceFactory.getReportService();
    }
}
