package com.foros.birt.services.birt.action;

import com.foros.birt.services.birt.ActionHandlerFactory;
import com.foros.birt.services.birt.BirtReportHelperService;
import com.foros.model.report.birt.BirtReportSession;
import com.foros.session.birt.BirtReportService;

import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.actionhandler.BirtChangeParameterActionHandler;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("ChangeParameterActionHandler")
@Scope("prototype")
public class ChangeParameterActionHandler extends BirtChangeParameterActionHandler {

    @Autowired
    private BirtReportService birtReportService;

    @Autowired
    private BirtReportHelperService birtReportHelperService;

    @Autowired
    private ActionHandlerFactory actionHandlerFactory;

    private BirtReportSession session;

    public ChangeParameterActionHandler(BirtReportSession session, IContext context,
                                        Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }

    protected void runReport() throws Exception {
        actionHandlerFactory.createRunReportActionHandler(session, context, operation, response).execute();
    }

}
