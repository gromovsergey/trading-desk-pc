package com.foros.birt.services.birt.action;

import com.foros.model.report.birt.BirtReportSession;

import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.service.actionhandler.BirtGetCascadeParameterActionHandler;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("GetCascadingParameterActionHandler")
@Scope("prototype")
public class GetCascadeParameterActionHandler extends BirtGetCascadeParameterActionHandler {

    private BirtReportSession session;

    public GetCascadeParameterActionHandler(BirtReportSession session, IContext context,
                                            Operation operation, GetUpdatedObjectsResponse response) {
        super(context, operation, response);
        this.session = session;
    }


}
