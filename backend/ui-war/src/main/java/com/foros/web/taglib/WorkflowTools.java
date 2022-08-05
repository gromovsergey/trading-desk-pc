package com.foros.web.taglib;

import com.foros.model.ApprovableEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.ServiceLocator;
import com.foros.session.StatusAction;
import com.foros.session.status.ApprovalAction;
import com.foros.session.workflow.ApprovalWorkflow;
import com.foros.session.workflow.StatusWorkflow;
import com.foros.session.workflow.WorkflowService;

public class WorkflowTools {

    public static StatusWorkflow getStatusWorkflow(StatusEntityBase entity) {
        WorkflowService workflowService = ServiceLocator.getInstance().lookup(WorkflowService.class);
        return workflowService.getStatusWorkflow(entity);
    }

    public static boolean isStatusActionAvailable(StatusWorkflow workflow, String action) {
        StatusAction statusAction = StatusAction.valueOf(action);
        return workflow.isActionAvailable(statusAction);
    }

    public static ApprovalWorkflow getApprovalWorkflow(ApprovableEntity entity) {
        WorkflowService workflowService = ServiceLocator.getInstance().lookup(WorkflowService.class);
        return workflowService.getApprovalWorkflow(entity);
    }

    public static boolean isQaActionAvailable(ApprovalWorkflow workflow, String action) {
        ApprovalAction statusAction = ApprovalAction.valueOf(action);
        return workflow.isActionAvailable(statusAction);
    }

    public static boolean isDeleted(StatusEntityBase entity) {
        return entity.getInheritedStatus().equals(Status.DELETED);
    }

    public static boolean isParentDeleted(StatusEntityBase entity) {
        return entity.getParentStatus().equals(Status.DELETED);
    }

/*
    public static boolean isInternal() {
        return SecurityContext.getPrincipal().getAccountRoleId().intValue() == AccountRole.INTERNAL.ordinal();
    }
*/

}
