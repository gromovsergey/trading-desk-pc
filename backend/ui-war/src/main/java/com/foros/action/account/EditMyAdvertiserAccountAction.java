package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.restriction.annotation.Restrict;

public class EditMyAdvertiserAccountAction extends EditAccountActionBase<AdvertisingAccountBase> implements AdvertiserSelfIdAware, AgencySelfIdAware  {

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = (AdvertisingAccountBase) accountService.getMyAccount();

        prepareFlagsForEdit();

        return SUCCESS;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        if (account == null) {
            account = new AdvertiserAccount();
        }
        account.setId(advertiserId);
    }

    @Override
    public void setAgencyId(Long agencyId) {
        if (account == null) {
            account = new AgencyAccount();
        }
        account.setId(agencyId);
    }
}
