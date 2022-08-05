package com.foros.action.account;

import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.CmpAccount;

public class SaveCMPAccountAction extends SaveAccountActionBase<CmpAccount> {

    public SaveCMPAccountAction() {
        account = new CmpAccount();
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
