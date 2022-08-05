package com.foros.action.account;

import com.foros.model.account.IspAccount;

public class SaveMyISPAccountAction extends SaveAccountActionBase<IspAccount> {

    public SaveMyISPAccountAction() {
        account = new IspAccount();
    }

    public String update() {
        account.setId(accountService.getMyAccount().getId());
        accountService.updateExternalAccount(account);

        return SUCCESS;
    }
}
