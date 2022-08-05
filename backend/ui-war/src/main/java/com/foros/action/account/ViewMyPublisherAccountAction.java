package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.PublisherAccount;
import com.foros.restriction.annotation.Restrict;

public class ViewMyPublisherAccountAction extends ViewAccountActionBase<PublisherAccount> {

    @ReadOnly
    @Restrict(restriction="Account.view", parameters="'Publisher'")
    public String view() {
        account = (PublisherAccount) accountService.getMyAccountWithTerms();

        return SUCCESS;
    }
}
