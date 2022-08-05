package com.foros.action.account;

import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.IspAccount;

public class SaveISPAccountAction extends SaveAccountActionBase<IspAccount> {
    public SaveISPAccountAction() {
        account = new IspAccount();
    }

    public String update() {
        prepareFlagsForSave();

        accountService.updateExternalAccount(account);

        return SUCCESS;
    }

    public String create() {
        prepareFlagsForSave();
        accountService.createExternalAccount(account);

        return SUCCESS;
    }
}
