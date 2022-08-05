package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.IspAccount;
import com.foros.restriction.annotation.Restrict;

public class EditISPAccountAction extends EditAccountActionBase<IspAccount> {

    public EditISPAccountAction() {
        account = new IspAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = accountService.viewIspAccount(account.getId());

        prepareFlagsForEdit();

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.create", parameters="'ISP'")
    public String create() {
        account = new IspAccount();
        account.setCurrency(currencyService.getDefault());

        return SUCCESS;
    }
}
