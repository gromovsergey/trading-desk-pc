package com.foros.birt.services.birt.action;

import com.foros.birt.web.util.ExportHelper;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.actionhandler.BirtCustomerExtractDataActionHandler;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("ExtractDataActionHandler")
@Scope("prototype")
public class ExtractDataActionHandler extends BirtCustomerExtractDataActionHandler {

    @Autowired
    private BirtReportService birtReportService;

    private BirtReportSession session;

    public ExtractDataActionHandler(BirtReportSession session, IContext context,
                                    Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    @Override
    protected void __execute() throws Exception {
        String documentFileName = birtReportService.getReportDocumentAbsoluteFileName(session.getBirtReportInstance());

        context.getBean().setReportDocumentName(documentFileName);

        ExportHelper.prepareExtract(context);

        super.__execute();
    }
}
