package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AgencyAccount;
import com.foros.restriction.annotation.Restrict;

public class EditAgencyAccountAction extends EditAdvertisingAccountActionBase<AgencyAccount> {

    public EditAgencyAccountAction() {
        account = new AgencyAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = accountService.viewAgencyAccount(account.getId());

        prepareFlagsForEdit();
        prepareContractDateForEdit();

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction="Account.create", parameters="'Agency'")
    public String create() {
        account = new AgencyAccount();
        account.setCurrency(currencyService.getDefault());

        return SUCCESS;
    }
}
