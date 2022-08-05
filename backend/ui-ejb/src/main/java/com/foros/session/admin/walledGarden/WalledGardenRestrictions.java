package com.foros.session.admin.walledGarden;

import static com.foros.security.AccountRole.INTERNAL;

import com.foros.model.admin.WalledGarden;
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
    @Permission(objectType = "walledGarden", action = "create", accountRoles = { INTERNAL }),
    @Permission(objectType = "walledGarden", action = "edit", accountRoles = { INTERNAL }),
    @Permission(objectType = "walledGarden", action = "view", accountRoles = { INTERNAL })
})
public class WalledGardenRestrictions {
    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public boolean canCreate() {
        return permissionService.isGranted("walledGarden", "create");
    }

    private boolean canAccessAccounts(WalledGarden walledGarden) {
        if (walledGarden.getAgency() != null && walledGarden.getAgency().getId() != null && !entityRestrictions.canAccess(walledGarden.getAgency())) {
            return false;
        }
        if (walledGarden.getPublisher() != null && walledGarden.getPublisher().getId() != null && !entityRestrictions.canAccess(walledGarden.getPublisher())) {
            return false;
        }
        return true;
    }

    @Restriction
    public boolean canCreate(WalledGarden walledGarden) {
        return canCreate() && canAccessAccounts(walledGarden);
    }

    @Restriction
    public boolean canUpdate() {
        return permissionService.isGranted("walledGarden", "edit");
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("walledGarden", "view");
    }
}
