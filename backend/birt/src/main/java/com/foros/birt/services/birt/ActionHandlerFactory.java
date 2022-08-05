package com.foros.birt.services.birt;

import com.foros.model.report.birt.BirtReportSession;

import org.eclipse.birt.report.context.BirtContext;
import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.actionhandler.IActionHandler;
import org.eclipse.birt.report.soapengine.api.Data;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.eclipse.birt.report.soapengine.api.Oprand;
import org.eclipse.birt.report.soapengine.api.ReportId;
import org.eclipse.birt.report.soapengine.api.ReportIdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ActionHandlerFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public IActionHandler createActionHandler(String operator, BirtReportSession session, IContext context,
                                              Operation operation, GetUpdatedObjectsResponse response) {

        return (IActionHandler) applicationContext.getBean(operator + "ActionHandler", session, context, operation, response);
    }

    public IActionHandler createActionHandler(String operator, BirtReportSession session, IContext context) {
        return createActionHandler(operator, session, context, null, null);
    }

    public IActionHandler createRunReportActionHandler(BirtReportSession session, IContext context,
                                                       Operation operation, GetUpdatedObjectsResponse response) {
        return createActionHandler("RunReport", session, context, operation, response);
    }

    public IActionHandler createRenderActionHandler(BirtReportSession session, IContext context,
                                                    Operation operation, GetUpdatedObjectsResponse response) {
        return createActionHandler("RenderReport", session, context, operation, response);
    }

    public IActionHandler createExportActionHandler(BirtReportSession session, BirtContext context) {
        return createActionHandler("Export", session, context);
    }

    public IActionHandler createExtractDataActionHandler(BirtReportSession session, BirtContext context) {
        Operation operation = new Operation(
                new ReportId("Document", ReportIdType.Document, 0L), "ExtractData", new Oprand[0], new Data()
        );

        return createActionHandler("ExtractData", session, context, operation, new GetUpdatedObjectsResponse());
    }
}
