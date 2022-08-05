package com.foros.session.status;

import com.foros.model.ApprovableEntity;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.StatusChangeDateAware;
import com.foros.model.StatusEntityBase;
import com.foros.model.security.ActionType;
import com.foros.model.security.ObjectType;
import com.foros.model.security.Statusable;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.BusinessException;
import com.foros.session.StatusAction;
import com.foros.session.security.AuditService;
import com.foros.session.workflow.StatusWorkflow;
import com.foros.session.workflow.WorkflowService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless(name = "StatusService")
public class StatusServiceBean implements StatusService {
    @EJB
    private AuditService auditService;

    @EJB
    private WorkflowService workflowService;

    @EJB
    private DisplayStatusService displayStatusService;
    
    @EJB
    private PermissionService permissionService;

    @Override
    public <T extends StatusEntityBase> void delete(T entity) {
        checkForDeletedAndSetStatus(entity, StatusAction.DELETE);
    }

    @Override
    public <T extends StatusEntityBase> void undelete(T entity) {
        checkForDeletedParentAndSetStatus(entity, StatusAction.UNDELETE);
    }

    @Override
    public <T extends StatusEntityBase> void activate(T entity) {
        checkForDeletedAndSetStatus(entity, StatusAction.ACTIVATE);
    }

    @Override
    public <T extends StatusEntityBase> void inactivate(T entity) {
        checkForDeletedAndSetStatus(entity, StatusAction.INACTIVATE);
    }

    private <T extends StatusEntityBase> void checkForDeletedAndSetStatus(T entity, StatusAction statusAction) {
        if (entity.getInheritedStatus() == Status.DELETED) {
            throw new BusinessException("Cannot operate with deleted own entity: " + entity);
        }

        setStatus(entity, statusAction);
    }

    private <T extends StatusEntityBase> void checkForDeletedParentAndSetStatus(T entity, StatusAction statusAction) {
        if (entity.getParentStatus() == Status.DELETED) {
            throw new BusinessException("Cannot operate with deleted own entity: " + entity);
        }

        setStatus(entity, statusAction);
    }

    protected <T extends StatusEntityBase> void setStatus(T entity, StatusAction statusAction) {
        StatusWorkflow workflow = workflowService.getStatusWorkflow(entity);
        entity.setStatus(workflow.doAction(statusAction));

        if (entity instanceof StatusChangeDateAware) {
            ((StatusChangeDateAware)entity).setStatusChangeDate(new Date());
        }

        if (entity instanceof Identifiable && ObjectType.valueOf(entity.getClass()) != null) {
            auditService.audit(entity, ActionType.UPDATE);
        }

        if ((entity instanceof DisplayStatusEntityBase) && (entity instanceof Identifiable)) {
            displayStatusService.update((Identifiable)entity);
        }
    }

    @Override
    public boolean isActionAvailable(Statusable entity, StatusAction action) {
        return workflowService.getStatusWorkflow(entity).isActionAvailable(action);
    }

    public void makePendingOnChange(ApprovableEntity entity, boolean isStatusChanged) {
        // Commented for now. See OUI-25720
//        if (isStatusChanged && Status.ACTIVE == entity.getStatus()
//                && permissionService.isGranted("advertiser_entity", "activate")) {
//            return;
//        }
//
//        boolean isStatusAllowUpdate = Status.ACTIVE == entity.getStatus() && entity.isStatusAllowed(Status.PENDING);
//
//        if (SecurityContext.isInternal() && isStatusAllowUpdate) {
//            entity.setStatus(Status.PENDING);
//        }
    }
}
