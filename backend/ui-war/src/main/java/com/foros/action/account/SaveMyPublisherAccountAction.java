package com.foros.action.account;

import com.foros.model.account.PublisherAccount;

public class SaveMyPublisherAccountAction extends SaveAccountActionBase<PublisherAccount> {

    public SaveMyPublisherAccountAction() {
        account = new PublisherAccount();
    }

    public String update() {
        account.setId(accountService.getMyAccount().getId());
        accountService.updateExternalAccount(account);

        return SUCCESS;
    }
}
