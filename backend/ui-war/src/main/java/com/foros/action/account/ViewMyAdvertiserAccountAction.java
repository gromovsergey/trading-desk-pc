package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.restriction.annotation.Restrict;

public class ViewMyAdvertiserAccountAction extends ViewAdvertiserAccountActionBase {
    @ReadOnly
    @Restrict(restriction="Account.view", parameters="'Advertiser'")
    public String view() {
        account = (AdvertisingAccountBase) accountService.getMyAccountWithTerms();

        return SUCCESS;
    }
}
