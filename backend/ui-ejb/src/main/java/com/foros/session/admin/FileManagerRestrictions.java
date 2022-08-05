package com.foros.session.admin;

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
        @Permission(objectType = "fileManager", action = "edit", accountRoles = {INTERNAL})
})
public class FileManagerRestrictions {
    @EJB
    private PermissionService permissionService;

    @Restriction
    public boolean canManage() {
        return permissionService.isGranted("fileManager", "edit");
    }
}
