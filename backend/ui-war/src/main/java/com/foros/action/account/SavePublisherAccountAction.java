package com.foros.action.account;

import com.foros.model.account.PublisherAccount;

public class SavePublisherAccountAction extends SaveAccountActionBase<PublisherAccount> {

    public SavePublisherAccountAction() {
        account = new PublisherAccount();
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
