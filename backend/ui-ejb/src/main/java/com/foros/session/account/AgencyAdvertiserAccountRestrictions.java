package com.foros.session.account;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
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
public class AgencyAdvertiserAccountRestrictions {
    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private AdvertisingAccountRestrictions advertisingAccountRestrictions;


    @Restriction
    public boolean canCreate(AgencyAccount agency) {
        return permissionService.isGranted("agency_advertiser_account", "create") &&
                entityRestrictions.canUpdate(agency);
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("agency_advertiser_account", "view");
    }

    @Restriction
    public boolean canView(AdvertiserAccount account) {
        return canView() && entityRestrictions.canView(account);
    }

    @Restriction
    public boolean canViewAny(AgencyAccount account) {
        return canView() && entityRestrictions.canView(account);
    }

    @Restriction
    public boolean canUpdate(AdvertiserAccount account) {
        return permissionService.isGranted("agency_advertiser_account", "edit") &&
                entityRestrictions.canUpdate(account) &&
                    advertisingAccountRestrictions.canUpdateCommission(account);
    }

    @Restriction
    public boolean canCreate(AdvertiserAccount account) {
        return permissionService.isGranted("agency_advertiser_account", "edit") &&
                entityRestrictions.canUpdate(account) &&
                    advertisingAccountRestrictions.canUpdateCommission(account);
    }

    @Restriction
    public boolean canDelete(AdvertiserAccount account) {
        return permissionService.isGranted("agency_advertiser_account", "edit") &&
                entityRestrictions.canDelete(account);
    }

    @Restriction
    public boolean canUndelete(AdvertiserAccount account) {
        return permissionService.isGranted("agency_advertiser_account", "undelete") &&
                entityRestrictions.canUndelete(account);
    }

    @Restriction
    public boolean canActivate(AdvertiserAccount account) {
        return permissionService.isGranted("agency_advertiser_account", "edit") &&
                entityRestrictions.canActivate(account);
    }

    @Restriction
    public boolean canInactivate(AdvertiserAccount account) {
        return permissionService.isGranted("agency_advertiser_account", "edit") &&
                entityRestrictions.canInactivate(account);
    }

}
