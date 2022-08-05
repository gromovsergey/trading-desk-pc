package com.foros.birt.services.birt.axis;

import com.foros.birt.services.birt.ActionHandlerFactory;
import com.foros.model.report.birt.BirtReportSession;

import org.eclipse.birt.report.context.BirtContext;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjects;
import org.eclipse.birt.report.soapengine.api.GetUpdatedObjectsResponse;
import org.eclipse.birt.report.soapengine.api.Operation;

public class InvokerImpl implements Invoker {

    private ActionHandlerFactory actionHandlerFactory;
    private BirtReportSession session;
    private BirtContext context;

    public InvokerImpl(ActionHandlerFactory actionHandlerFactory, BirtReportSession session, BirtContext context) {
        this.actionHandlerFactory = actionHandlerFactory;
        this.session = session;
        this.context = context;
    }

    @Override
    public GetUpdatedObjectsResponse invoke(GetUpdatedObjects request) throws Exception {
        GetUpdatedObjectsResponse response = new GetUpdatedObjectsResponse();

        for (Operation op : request.getOperation()) {
            actionHandlerFactory
                    .createActionHandler(op.getOperator(), session, context, op, response)
                    .execute();
        }

        return response;
    }

}
