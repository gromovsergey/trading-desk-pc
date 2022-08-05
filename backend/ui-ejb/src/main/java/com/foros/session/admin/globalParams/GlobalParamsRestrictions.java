package com.foros.session.admin.globalParams;

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
    @Permission(objectType = "globalParams", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "globalParams", action = "edit", accountRoles = {INTERNAL})
})
public class GlobalParamsRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("globalParams", "view");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("globalParams", "edit");
    }

}
