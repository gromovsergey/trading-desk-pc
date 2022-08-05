package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.PublisherAccount;
import com.foros.restriction.annotation.Restrict;

public class EditPublisherAccountAction extends EditAccountActionBase<PublisherAccount> {

    public EditPublisherAccountAction() {
        account = new PublisherAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = accountService.viewPublisherAccount(account.getId());

        prepareFlagsForEdit();

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.create", parameters="'Publisher'")
    public String create() {
        account = new PublisherAccount();
        account.setCurrency(currencyService.getDefault());
        account.setPassbackBelowFold(true);
        account.setCreativesReapproval(false);
        return SUCCESS;
    }
}
