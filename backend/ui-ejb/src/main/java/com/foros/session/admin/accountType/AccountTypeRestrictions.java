package com.foros.session.admin.accountType;

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
    @Permission(objectType = "accountType", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "accountType", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "accountType", action = "edit", accountRoles = {INTERNAL})
})
public class AccountTypeRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("accountType", "view");
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("accountType", "create");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("accountType", "edit");
    }

}