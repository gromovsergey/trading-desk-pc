package com.foros.session.admin.categoryChannel;

import com.foros.model.Status;
import com.foros.model.channel.CategoryChannel;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.EntityTO;
import com.foros.session.GenericEntityService;
import com.foros.security.AccountRole;
import com.foros.session.StatusAction;
import com.foros.session.status.StatusService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "categoryChannel", action = "create", accountRoles = {AccountRole.INTERNAL}),
    @Permission(objectType = "categoryChannel", action = "edit", accountRoles = {AccountRole.INTERNAL}),
    @Permission(objectType = "categoryChannel", action = "view", accountRoles = {AccountRole.INTERNAL}),
    @Permission(objectType = "categoryChannel", action = "undelete", accountRoles = {AccountRole.INTERNAL})
})
public class CategoryChannelRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private StatusService statusService;

    @EJB
    private GenericEntityService genericEntityService;

    @EJB
    private CategoryChannelService categoryChannelService;

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("categoryChannel", "create");
    }

    @Restriction
    public boolean canCreate(CategoryChannel parent) {
        return canCreate() && (parent == null || !isDeleted(parent));
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("categoryChannel", "edit");
    }

    @Restriction
    public boolean canUpdate(CategoryChannel entity) {
        return permissionService.isGranted("categoryChannel", "edit") && !isDeleted(entity);
    }

    @Restriction
    public boolean canInactivate(CategoryChannel entity) {
        return canUpdate(entity) && statusService.isActionAvailable(entity, StatusAction.INACTIVATE);
    }

    @Restriction
    public boolean canActivate(CategoryChannel entity) {
        return canUpdate(entity) && statusService.isActionAvailable(entity, StatusAction.ACTIVATE);
    }

    @Restriction
    public boolean canDelete(CategoryChannel entity) {
        return canUpdate(entity) && statusService.isActionAvailable(entity, StatusAction.DELETE);
    }

    @Restriction
    public boolean canUndelete(CategoryChannel entity) {
        return permissionService.isGranted("categoryChannel", "undelete")
                && !isParentDeleted(entity)
                && statusService.isActionAvailable(entity, StatusAction.UNDELETE);
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("categoryChannel", "view");
    }

    private boolean isDeleted(CategoryChannel channel) {
        boolean deleted = genericEntityService.isDeleted(channel);
        if (!deleted) {
            List<EntityTO> ancestors = categoryChannelService.getChannelAncestorsChain(channel.getId(), false);
            for (EntityTO ancestor : ancestors) {
                if (ancestor.getStatus() == Status.DELETED) {
                    deleted = true;
                    break;
                }
            }
        }
        return deleted;
    }

    private boolean isParentDeleted(CategoryChannel channel) {
        boolean deleted = false;
        List<EntityTO> ancestors = categoryChannelService.getChannelAncestorsChain(channel.getId(), false);
        for (EntityTO ancestor : ancestors) {
            if (ancestor.getStatus() == Status.DELETED) {
                deleted = true;
                break;
            }
        }
        return deleted;
    }
 
}
