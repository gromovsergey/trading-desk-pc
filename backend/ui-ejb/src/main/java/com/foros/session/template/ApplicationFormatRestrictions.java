package com.foros.session.template;

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
    @Permission(objectType = "applicationFormat", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "applicationFormat", action = "edit", accountRoles = {INTERNAL}),
    @Permission(objectType = "applicationFormat", action = "view", accountRoles = {INTERNAL})
})
public class ApplicationFormatRestrictions {

    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("applicationFormat", "create");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("applicationFormat", "edit");
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("applicationFormat", "view");
    }

}
