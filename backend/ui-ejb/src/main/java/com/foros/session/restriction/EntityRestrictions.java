package com.foros.session.restriction;

import com.foros.model.Approvable;
import com.foros.model.OwnedApprovable;
import com.foros.model.Status;
import com.foros.model.account.ExternalAccount;
import com.foros.model.security.NotManagedEntity;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.OwnedStatusable;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.CurrentUserService;
import com.foros.session.GenericEntityService;
import com.foros.session.StatusAction;
import com.foros.session.status.ApprovalAction;
import com.foros.session.status.ApprovalService;
import com.foros.session.status.StatusService;
import com.foros.validation.ValidationContext;
import com.foros.validation.util.ValidationUtil;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class EntityRestrictions {
    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private GenericEntityService genericEntityService;

    @EJB
    private StatusService statusService;
    
    @EJB
    private ApprovalService approvalService;

    @EJB
    private PermissionService permissionService;

    public void canViewLog(ValidationContext context, OwnedStatusable entity) {
        if (!currentUserService.isInternal()) {
            context.addConstraintViolation("errors.operation.not.permitted");
        }
    }

    @Restriction
    public boolean canViewLog(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canViewLog(context, entity);
        return context.ok();
    }

    private void validateAccessRules(ValidationContext context, OwnedStatusable entity) {
        validateAccessRules(context, entity, NotManagedEntity.Util.isManaged(entity));
    }

    private void validateAccessRules(ValidationContext context, OwnedEntity entity, boolean isManaged) {
        switch (SecurityContext.getAccountRole()) {
            case INTERNAL:
                if (isManaged && currentUserService.isAccountManager() && entity.getAccount() instanceof ExternalAccount) {
                    if (!currentUserService.isManagerOf(entity)) {
                        context.addConstraintViolation("errors.not.entity.manager");
                    }
                } else {
                    if (!currentUserService.hasAccessTo(entity)) {
                        context.addConstraintViolation("errors.forbidden");
                    }
                }
                break;
            case AGENCY:
                if (currentUserService.isAgencyOf(entity)) {
                    if (!currentUserService.isAdvAccessGranted(entity)) {
                        context.addConstraintViolation("errors.forbidden");
                    }
                } else if (!currentUserService.isOwnerOf(entity)) {
                    context.addConstraintViolation("errors.not.entity.owner");
                }
                break;
            case PUBLISHER:
                if (currentUserService.isPublisherOf(entity)) {
                    if (!currentUserService.isSiteAccessGranted(entity)) {
                        context.addConstraintViolation("errors.forbidden");
                    }
                } else if (!currentUserService.isOwnerOf(entity)) {
                    context.addConstraintViolation("errors.not.entity.owner");
                }
                break;
            default:
                if (!currentUserService.isOwnerOf(entity)) {
                    context.addConstraintViolation("errors.not.entity.owner");
                }
        }
    }

    public void canView(ValidationContext context, OwnedStatusable entity) {
        validateBasicView(context, entity);
        validateAccessRules(context, entity);
    }

    private void validateBasicView(ValidationContext context, OwnedStatusable entity) {
        if (!currentUserService.isInternal()) {
            validateNotDeleted(context, entity);
        }
    }

    public void canCreate(ValidationContext context, OwnedStatusable entity, boolean isManaged) {
        validateNotDeleted(context, entity);
        validateAccessRules(context, entity, isManaged);
    }

    public void canUpdate(ValidationContext context, OwnedStatusable entity) {
        validateNotDeleted(context, entity);
        validateAccessRules(context, entity);
    }

    public boolean validateNotDeleted(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        validateNotDeleted(context, entity);
        return context.ok();
    }

    private void validateNotDeleted(ValidationContext context, OwnedStatusable entity) {
        if (genericEntityService.isDeleted(entity)) {
            context.addConstraintViolation("errors.entity.deleted");
        }
    }

    private void validateInternal(ValidationContext context) {
        if (!currentUserService.isInternal()) {
            context.addConstraintViolation("errors.operation.not.permitted");
        }
    }

    @Restriction
    public boolean canAccess(OwnedEntity entity) {
        ValidationContext context = ValidationUtil.createContext();
        validateAccessRules(context, entity, NotManagedEntity.Util.isManaged(entity));
        return context.ok();
    }

    @Restriction
    public boolean canView(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canView(context, entity);
        return context.ok();
    }

    public boolean canView(AccountRole accountRole) {
        boolean accountManager = currentUserService.isAccountManager();
        if (currentUserService.isInternal()) {
            switch (accountRole) {
                case INTERNAL:
                    return true; // Access to internal entities regulated by permissions
                case AGENCY:
                case ADVERTISER:
                    return !accountManager || currentUserService.isAdvertiserAccountManager();
                case CMP:
                    return !accountManager || currentUserService.isCMPAccountManager();
                case PUBLISHER:
                    return !accountManager || currentUserService.isPublisherAccountManager();
                case ISP:
                    return !accountManager || currentUserService.isISPAccountManager();
            }
            return false;
        } else {
            return accountRole == currentUserService.getAccountRole();
        }
    }

    public boolean canViewBasic(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        validateBasicView(context, entity);
        return context.ok();
    }

    public boolean canUpdate(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canUpdate(context, entity);
        return context.ok();
    }
    
    public boolean canCreate(OwnedStatusable entity, boolean isManaged) {
        ValidationContext context = ValidationUtil.createContext();
        canCreate(context, entity, isManaged);
        return context.ok();
    }

    public void canCreateCopy(ValidationContext context, OwnedStatusable entityToCopy, OwnedStatusable parent) {
        canUpdate(context, entityToCopy);
        canCreate(context, parent, NotManagedEntity.Util.isManaged(entityToCopy));
    }

    public boolean canCreateCopy(OwnedStatusable entityToCopy, OwnedStatusable parent) {
        return canUpdate(entityToCopy) && canCreate(parent, NotManagedEntity.Util.isManaged(entityToCopy));
    }

    private void validateChangeStatusByWorkflow(ValidationContext context, OwnedStatusable entity, StatusAction action) {
        if (!statusService.isActionAvailable(entity, action)) {
            context
                    .addConstraintViolation("error.change.status.not.allowed")
                    .withPath("status")
                    .withParameters(action);
        }
    }

    private void validateChangeStatus(ValidationContext context, OwnedStatusable entity, StatusAction action) {
        validateNotDeleted(context, entity);
        validateChangeStatusByWorkflow(context, entity, action);
        validateAccessRules(context, entity);
    }

    private void validateParentNotDeleted(ValidationContext context, OwnedStatusable entity) {
        if (entity.getParentStatus().equals(Status.DELETED)) {
            context.addConstraintViolation("error.parent.deleted");
        }
    }

    public void canDelete(ValidationContext context, OwnedStatusable entity) {
        validateChangeStatus(context, entity, StatusAction.DELETE);
    }

    public boolean canDelete(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canDelete(context, entity);
        return context.ok();
    }

    public void canUndelete(ValidationContext context, OwnedStatusable entity) {
        validateInternal(context);
        validateParentNotDeleted(context, entity);
        validateChangeStatusByWorkflow(context, entity, StatusAction.UNDELETE);
        validateAccessRules(context, entity);
    }

    public boolean canUndelete(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canUndelete(context, entity);
        return context.ok();
    }

    public void canUndeleteChildren(ValidationContext context, OwnedStatusable entity) {
        validateInternal(context);
        validateNotDeleted(context, entity);
        validateAccessRules(context, entity);
    }

    public boolean canUndeleteChildren(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canUndeleteChildren(context, entity);
        return context.ok();
    }

    public void canActivate(ValidationContext context, OwnedStatusable entity) {
        validateChangeStatus(context, entity, StatusAction.ACTIVATE);
    }

    public boolean canActivate(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canActivate(context, entity);
        return context.ok();
    }

    public void canInactivate(ValidationContext context, OwnedStatusable entity) {
        validateChangeStatus(context, entity, StatusAction.INACTIVATE);
    }

    public boolean canInactivate(OwnedStatusable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canInactivate(context, entity);
        return context.ok();
    }

    private void validateApprovalByWorkflow(ValidationContext context, Approvable entity, ApprovalAction action) {
        if (!approvalService.isActionAvailable(entity, action)) {
            context
                    .addConstraintViolation("error.approval.not.allowed")
                    .withPath("approveStatus");
        }
    }

    private void validateApproval(OwnedApprovable entity, ValidationContext context, ApprovalAction action) {
        validateNotDeleted(context, entity);
        validateApprovalByWorkflow(context, entity, action);
        validateAccessRules(context, entity);
    }

    public void canApprove(ValidationContext context, OwnedApprovable entity) {
        validateApproval(entity, context, ApprovalAction.APPROVE);
    }

    public boolean canApprove(OwnedApprovable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canApprove(context, entity);
        return context.ok();
    }

    public void canDecline(ValidationContext context, OwnedApprovable entity) {
        validateApproval(entity, context, ApprovalAction.DECLINE);
    }

    public boolean canDecline(OwnedApprovable entity) {
        ValidationContext context = ValidationUtil.createContext();
        canDecline(context, entity);
        return context.ok();
    }

    public void validatePermission(ValidationContext context, String type, String action) {
        if (!permissionService.isGranted(type, action)) {
            context.addConstraintViolation("errors.operation.not.permitted");
        }
    }

}
