package com.foros.session.admin.wdRequestMapping;

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
    @Permission(objectType = "wdRequestMapping", action = "view", accountRoles = {INTERNAL}),
    @Permission(objectType = "wdRequestMapping", action = "create", accountRoles = {INTERNAL}),
    @Permission(objectType = "wdRequestMapping", action = "edit", accountRoles = {INTERNAL})
})
public class WDRequestMappingRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("wdRequestMapping", "view");
    }

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("wdRequestMapping", "create");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("wdRequestMapping", "edit");
    }

}
