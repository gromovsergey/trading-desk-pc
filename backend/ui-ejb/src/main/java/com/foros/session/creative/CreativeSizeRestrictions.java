package com.foros.session.creative;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.creative.CreativeSize;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.GenericEntityService;
import com.foros.session.StatusAction;
import com.foros.session.UtilityService;
import com.foros.session.status.StatusService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "creativeSize", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "creativeSize", action = "edit", accountRoles = {INTERNAL}),
    @Permission(objectType = "creativeSize", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "creativeSize", action = "undelete", accountRoles = {INTERNAL})
})
public class CreativeSizeRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private StatusService statusService;

    @EJB
    private GenericEntityService genericEntityService;

    @EJB
    private UtilityService utilityService;

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("creativeSize", "create");
    }

    @Restriction
    public boolean canCreateCopy(CreativeSize entity) {
        return canCreate() && canUpdateInternal(entity);
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("creativeSize", "edit");
    }

    @Restriction
    public boolean canUpdate(CreativeSize entity) {
        return canUpdateInternal(entity) && isNotTextSize(entity);
    }

    @Restriction
    public boolean canUpdateOptions(CreativeSize entity) {
        return canUpdateInternal(entity);
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("creativeSize", "view");
    }

    @Restriction
    public boolean canDelete(CreativeSize entity) {
        return permissionService.isGranted("creativeSize", "edit") && statusService.isActionAvailable(entity, StatusAction.DELETE) &&
                isNotTextSize(entity);
    }

    @Restriction
    public boolean canDeleteOptions(CreativeSize entity) {
        return canDelete(entity);
    }

    @Restriction
    public boolean canUndelete(CreativeSize entity) {
        return permissionService.isGranted("creativeSize", "undelete") && statusService.isActionAvailable(entity, StatusAction.UNDELETE) &&
            isNotTextSize(entity);
    }

    private boolean canUpdateInternal(CreativeSize entity) {
        return canUpdate() && !genericEntityService.isDeleted(entity);
    }

    private boolean isNotTextSize(CreativeSize entity) {
        CreativeSize size = utilityService.find(CreativeSize.class, entity.getId());
        return !CreativeSize.TEXT_SIZE.equals(size.getDefaultName());
    }
}
