package com.foros.session.campaignAllocation;

import static com.foros.security.AccountRole.INTERNAL;
import com.foros.model.campaign.Campaign;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.session.UtilityService;
import com.foros.session.campaignCredit.CampaignCreditAllocationService;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "campaignAllocation", action = "edit", accountRoles = {INTERNAL}),
        @Permission(objectType = "campaignAllocation", action = "view", accountRoles = {INTERNAL})
})
public class CampaignAllocationRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private UtilityService utilityService;

    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @Restriction
    public boolean canCreateUpdate(Campaign campaign) {
        return permissionService.isGranted("campaignAllocation", "edit")
                && entityRestrictions.canUpdate(campaign)
                && isIoManagementEnabled(campaign);
    }

    @Restriction
    public boolean canView(Campaign campaign) {
        return canView() && entityRestrictions.canView(campaign) &&
                (isIoManagementEnabled(campaign) || campaignCreditAllocationService.hasAllocations(campaign.getId()));
    }

    @Restriction
    public boolean canView(Long campaignId) {
        return canView(utilityService.find(Campaign.class, campaignId));
    }

    @Restriction
    public boolean canView() {
        return permissionService.isGranted("campaignAllocation", "view");
    }

    private Boolean isIoManagementEnabled(Campaign campaign) {
        return campaign.getAccount().getAccountType().getIoManagement();
    }
}
