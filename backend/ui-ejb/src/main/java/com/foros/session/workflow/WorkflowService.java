package com.foros.session.workflow;

import javax.ejb.Local;

import com.foros.model.Approvable;
import com.foros.model.security.Statusable;

@Local
public interface WorkflowService {
    ApprovalWorkflow getApprovalWorkflow(Approvable approvable);
    
    StatusWorkflow getStatusWorkflow(Statusable statusable);
}
