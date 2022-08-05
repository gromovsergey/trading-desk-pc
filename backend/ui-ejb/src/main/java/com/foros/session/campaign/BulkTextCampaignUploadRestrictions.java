package com.foros.session.campaign;

import com.foros.model.account.AdvertiserAccount;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class BulkTextCampaignUploadRestrictions {

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @Restriction
    public boolean canExport(AdvertiserAccount account) {
        return advertiserEntityRestrictions.canView(account)
                && isTextAdEnabled(account);
    }

    @Restriction
    public boolean canUpload(AdvertiserAccount account) {
        return advertiserEntityRestrictions.canCreate(account)
                && advertiserEntityRestrictions.canUpdate()
                && isTextAdEnabled(account);
    }

    private boolean isTextAdEnabled(AdvertiserAccount account) {
        return account.getAccountType().isAllowTextAdvertisingFlag();
    }
}
