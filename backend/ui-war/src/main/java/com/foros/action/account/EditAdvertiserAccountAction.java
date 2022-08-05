package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertiserAccount;
import com.foros.restriction.annotation.Restrict;

public class EditAdvertiserAccountAction extends EditAdvertiserAccountActionBase {

    public EditAdvertiserAccountAction() {
        account = new AdvertiserAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = accountService.viewAdvertiserAccount(account.getId());

        prepareFlagsForEdit();
        prepareContractDateForEdit();

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.create", parameters="'Advertiser'")
    public String create() {
        account = new AdvertiserAccount();
        account.setCurrency(currencyService.getDefault());

        return SUCCESS;
    }
}
