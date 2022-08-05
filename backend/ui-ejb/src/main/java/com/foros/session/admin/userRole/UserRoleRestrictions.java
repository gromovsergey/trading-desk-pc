package com.foros.session.admin.userRole;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "userRole", action = "view", accountRoles = {INTERNAL}),
        @Permission(objectType = "userRole", action = "create", accountRoles = {INTERNAL}),
        @Permission(objectType = "userRole", action = "edit", accountRoles = {INTERNAL})
})
public class UserRoleRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("userRole", "view");
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("userRole", "create");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("userRole", "edit");
    }
}
