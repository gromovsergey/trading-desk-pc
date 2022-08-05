package com.foros.session.creative;

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
    @Permission(objectType = "creativeCategory", action = "edit", accountRoles = {INTERNAL}),
    @Permission(objectType = "creativeCategory", action = "view", accountRoles = {INTERNAL})
})
public class CreativeCategoryRestrictions {

    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("creativeCategory", "edit");
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("creativeCategory", "view");
    }

    @Restriction
    public boolean canDelete() {
        return permissionService.isGranted("creativeCategory", "edit");
    }

}
