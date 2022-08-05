package com.foros.session.campaignCredit;

import com.foros.model.account.Account;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.foros.security.AccountRole.ADVERTISER;
import static com.foros.security.AccountRole.AGENCY;
import static com.foros.security.AccountRole.INTERNAL;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "campaignCredit", action = "view", accountRoles = {INTERNAL, AGENCY, ADVERTISER}),
        @Permission(objectType = "campaignCredit", action = "edit", accountRoles = {INTERNAL}),
        @Permission(objectType = "campaignCredit", action = "edit_allocations", accountRoles = {INTERNAL, AGENCY, ADVERTISER})
})
public class CampaignCreditRestrictions {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("campaignCredit", "view");
    }

    @Restriction
    public boolean canView(Account account) {
        return canView() && entityRestrictions.canAccess(account);
    }

    @Restriction
    public boolean canView(CampaignCredit campaignCredit) {
        return canView() && entityRestrictions.canAccess(campaignCredit);
    }

    @Restriction
    public boolean canEdit() {
        return permissionService.isGranted("campaignCredit", "edit");
    }

    @Restriction
    public boolean canEdit(Account account) {
        return canEdit() && entityRestrictions.canUpdate(account);
    }

    @Restriction
    public boolean canEdit(CampaignCredit campaignCredit) {
        return canEdit() && entityRestrictions.canUpdate(campaignCredit.getAccount());
    }

    @Restriction
    public boolean canDelete(Long campaignCreditId) {
        CampaignCredit campaignCredit = em.find(CampaignCredit.class, campaignCreditId);
        if (campaignCredit != null) {
            return canEdit(campaignCredit);
        }
        return true;
    }

    @Restriction
    public boolean canEditAllocations() {
        return permissionService.isGranted("campaignCredit", "edit_allocations");
    }

    @Restriction
    public boolean canEditAllocations(CampaignCredit campaignCredit) {
        return canEditAllocations() && entityRestrictions.canUpdate(campaignCredit.getAccount());
    }

    @Restriction
    public boolean canEditAllocations(CampaignCreditAllocation allocation) {
        return canEditAllocations() && entityRestrictions.canUpdate(allocation.getAccount());
    }
}
