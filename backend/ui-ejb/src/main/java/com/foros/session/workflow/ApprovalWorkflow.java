package com.foros.session.workflow;

import com.foros.model.ApproveStatus;
import com.foros.session.status.ApprovalAction;
import com.foros.util.workflow.Workflow;
import com.foros.util.workflow.WorkflowScheme;

public class ApprovalWorkflow extends Workflow<ApproveStatus, ApprovalAction> {
    public ApprovalWorkflow(WorkflowScheme<ApproveStatus, ApprovalAction> scheme, ApproveStatus step) {
    	super(scheme, step);
    }
}
