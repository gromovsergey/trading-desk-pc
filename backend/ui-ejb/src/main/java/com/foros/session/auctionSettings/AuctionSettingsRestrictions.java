package com.foros.session.auctionSettings;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.model.security.OwnedStatusable;
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
        @Permission(objectType = "auctionSettings", action = "view", accountRoles = {INTERNAL}),
        @Permission(objectType = "auctionSettings", action = "edit", accountRoles = {INTERNAL})
})
public class AuctionSettingsRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("auctionSettings", "view");
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("auctionSettings", "edit");
    }

    @Restriction
    public boolean canUpdate(OwnedStatusable entity) {
        return canUpdate() && entityRestrictions.canUpdate(entity);
    }
}
