package com.foros.session.admin.searchEngine;

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
        @Permission(objectType = "searchEngine", action = "view", accountRoles = {AccountRole.INTERNAL}),
        @Permission(objectType = "searchEngine", action = "create", accountRoles = {AccountRole.INTERNAL}),
        @Permission(objectType = "searchEngine", action = "edit", accountRoles = {AccountRole.INTERNAL})
})
public class SearchEngineRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView(){
        return permissionService.isGranted("searchEngine","view");
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("searchEngine", "create");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("searchEngine", "edit");
    }
}
