package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.CmpAccount;
import com.foros.restriction.annotation.Restrict;

public class EditCMPAccountAction extends EditAccountActionBase<CmpAccount> {

    public EditCMPAccountAction() {
        account = new CmpAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = accountService.viewCmpAccount(account.getId());

        prepareFlagsForEdit();

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.create", parameters="'CMP'")
    public String create() {
        account = new CmpAccount();
        account.setCurrency(currencyService.getDefault());

        return SUCCESS;
    }
}
