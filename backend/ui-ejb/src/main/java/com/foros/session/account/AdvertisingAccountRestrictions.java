package com.foros.session.account;

import com.foros.model.Country;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.security.AccountRole;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.restriction.EntityRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class AdvertisingAccountRestrictions {
    @EJB
    private PermissionService permissionService;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Restriction
    public boolean canUpdateRestrictedFinanceFields(AdvertisingAccountBase account) {
        return entityRestrictions.canUpdate(account) && accountRestrictions.isUpdateFinanceGranted(account);
    }

    public boolean canUpdateCommission(AdvertisingAccountBase account) {
        return AccountRole.AGENCY.equals(account.getRole()) ||
                AccountRole.ADVERTISER.equals(account.getRole()) && !account.isStandalone();
    }

    private boolean canUpdateTaxVatNumber(AdvertisingAccountBase account) {
        Country country = account.getCountry();
        return country.isVatEnabled() && country.isVatNumberInputEnabled();
    }

    private boolean canUpdateBillingContact(AdvertisingAccountBase account) {
        return !account.getAccountType().isPerCampaignInvoicingFlag();
    }

    @Restriction
    public boolean canUpdateFinance(AdvertisingAccountBase account) {
        return canUpdateRestrictedFinanceFields(account) ||
                (accountRestrictions.canUpdate(account) &&
                        (canUpdateCommission(account) || canUpdateTaxVatNumber(account) || canUpdateBillingContact(account)));
    }


    @Restriction
    public boolean canView() {
        return permissionService.isGranted("advertising_account", "view");
    }

    @Restriction
    public boolean canViewList() {
        return canView() || advertiserEntityRestrictions.canView();
    }

}
