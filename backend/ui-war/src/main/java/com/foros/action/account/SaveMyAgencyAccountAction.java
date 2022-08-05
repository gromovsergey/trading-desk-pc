package com.foros.action.account;

import com.foros.model.account.AgencyAccount;

public class SaveMyAgencyAccountAction extends SaveAccountActionBase<AgencyAccount> {

    public SaveMyAgencyAccountAction() {
        account = new AgencyAccount();
    }

    public String update() {
        account.setId(accountService.getMyAccount().getId());
        accountService.updateAgencyAccount(account);

        return SUCCESS;
    }
}
