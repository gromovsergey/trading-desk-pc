package com.foros.session.admin.bannedChannel;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "bannedChannel", action = "view", accountRoles = {AccountRole.INTERNAL}),
        @Permission(objectType = "bannedChannel", action = "edit", accountRoles = {AccountRole.INTERNAL})
})
public class BannedChannelRestrictions {

    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("bannedChannel", "view");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("bannedChannel", "edit");
    }
}
