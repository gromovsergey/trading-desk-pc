package com.foros.birt.services.birt.action;

import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.actionhandler.BirtQueryExportActionHandler;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("QueryExportActionHandler")
@Scope("prototype")
public class QueryExportActionHandler extends BirtQueryExportActionHandler {

    @Autowired
    private BirtReportService birtReportService;

    private BirtReportSession session;

    public QueryExportActionHandler(BirtReportSession session, IContext context,
                                    Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    @Override
    protected void __execute() throws Exception {
        context.getBean().setReportDocumentName(birtReportService.getReportDocumentAbsoluteFileName(session.getBirtReportInstance()));
        super.__execute();
    }
}
