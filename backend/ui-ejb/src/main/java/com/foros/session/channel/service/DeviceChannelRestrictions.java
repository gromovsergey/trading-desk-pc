package com.foros.session.channel.service;

import com.foros.model.Status;
import com.foros.model.channel.DeviceChannel;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.GenericEntityService;
import com.foros.session.StatusAction;
import com.foros.session.status.StatusService;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
    @Permission(objectType = "deviceChannel", action = "create", accountRoles = {AccountRole.INTERNAL}),
    @Permission(objectType = "deviceChannel", action = "edit", accountRoles = {AccountRole.INTERNAL}),
    @Permission(objectType = "deviceChannel", action = "view", accountRoles = {AccountRole.INTERNAL}),
    @Permission(objectType = "deviceChannel", action = "undelete", accountRoles = {AccountRole.INTERNAL})
})
public class DeviceChannelRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private StatusService statusService;

    @EJB
    private GenericEntityService genericEntityService;

    @EJB
    private DeviceChannelService deviceChannelService;

    @EJB
    private CurrentUserService currentUserService;

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("deviceChannel", "create");
    }

    @Restriction
    public boolean canCreate(DeviceChannel parent) {
        return canCreate() && parent != null && !parent.isBrowsers() && !parent.isApplications() && !isDeleted(parent);
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("deviceChannel", "edit");
    }

    @Restriction
    public boolean canUpdate(DeviceChannel entity) {
        return canUpdate() && !isDeleted(entity);
    }

    @Restriction
    public boolean canInactivate(DeviceChannel entity) {
        return canUpdate(entity) && statusService.isActionAvailable(entity, StatusAction.INACTIVATE) && !isRootChannel(entity);
    }

    @Restriction
    public boolean canActivate(DeviceChannel entity) {
        return canUpdate(entity) && statusService.isActionAvailable(entity, StatusAction.ACTIVATE) && isParentActive(entity);
    }

    @Restriction
    public boolean canDelete(DeviceChannel entity) {
        return canUpdate(entity) && statusService.isActionAvailable(entity, StatusAction.DELETE) && !isRootChannel(entity);
    }

    @Restriction
    public boolean canUndelete(DeviceChannel entity) {
        return permissionService.isGranted("deviceChannel", "undelete")
                && !isParentDeleted(entity)
                && statusService.isActionAvailable(entity, StatusAction.UNDELETE);
    }

    private boolean isRootChannel(DeviceChannel entity) {
        return entity.getParentChannel() == null;
    }

    private boolean isParentDeleted(DeviceChannel entity) {
        if (entity.getParentChannel() == null) {
            return false;
        }
        return Status.DELETED.equals(entity.getParentChannel().getStatus());
    }

    private boolean isParentActive(DeviceChannel entity) {
        if (entity.getParentChannel() == null) {
            return false;
        }
        return Status.ACTIVE.equals(entity.getParentChannel().getStatus());
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("deviceChannel", "view");
    }

    @Restriction
    public boolean canGet() {
        return canView() && currentUserService.getAccountRole() == AccountRole.INTERNAL;
    }

    private boolean isDeleted(DeviceChannel channel) {
        boolean deleted = genericEntityService.isDeleted(channel);
        if (!deleted) {
            List<EntityTO> ancestors = deviceChannelService.getChannelAncestorsChain(channel.getId(), false);
            for (EntityTO ancestor : ancestors) {
                if (ancestor.getStatus() == Status.DELETED) {
                    deleted = true;
                    break;
                }
            }
        }
        return deleted;
    }
}
