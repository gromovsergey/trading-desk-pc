package com.foros.session.channel.service;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;


@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "placementsBlacklist", action = "view", accountRoles = {INTERNAL}),
        @Permission(objectType = "placementsBlacklist", action = "edit", accountRoles = {INTERNAL})
})
public class PlacementsBlacklistRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("placementsBlacklist", "view");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("placementsBlacklist", "edit");
    }

}
