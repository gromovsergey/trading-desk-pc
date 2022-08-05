package com.foros.session.workflow;

import com.foros.model.Status;
import com.foros.session.StatusAction;
import com.foros.util.workflow.Workflow;
import com.foros.util.workflow.WorkflowScheme;

public class StatusWorkflow extends Workflow<Status, StatusAction> {
    public StatusWorkflow(WorkflowScheme<Status, StatusAction> scheme, Status step) {
        super(scheme, step);
    }

}
