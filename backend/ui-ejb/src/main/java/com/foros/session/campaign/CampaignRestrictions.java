package com.foros.session.campaign;


import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignType;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Restrictions
public class CampaignRestrictions {
    @EJB
    private PermissionService permissionService;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public boolean canCreate(Campaign campaign) {
        Account account = em.find(Account.class, campaign.getAccount().getId());

        if (account == null) {
            throw new EntityNotFoundException("Account with id = " + campaign.getAccount().getId() + " not found");
        }

        boolean isCampaignAllowed = campaign.getCampaignType() == CampaignType.DISPLAY
                ? advertiserEntityRestrictions.canAccessDisplayAd(account)
                : advertiserEntityRestrictions.canAccessTextAd(account);

        return isCampaignAllowed && advertiserEntityRestrictions.canCreate(account);
    }

    @Restriction
    public boolean canViewAvailableAccountCredit(Campaign campaign) {
        AdvertiserAccount account = campaign.getAccount();
        if (account.isStandalone() || account.getAccountType().isAgencyFinancialFieldsFlag()) {
            return permissionService.isGranted("advertising_account", "view");
        }
        return permissionService.isGranted("agency_advertiser_account", "view");
    }

    @Restriction
    public boolean canViewCommission(Campaign campaign) {
        if (campaign.getAccount().isStandalone()) {
            return false;
        }
        return permissionService.isGranted("advertising_account", "view") || advertiserEntityRestrictions.canUpdate(campaign);
    }

}
