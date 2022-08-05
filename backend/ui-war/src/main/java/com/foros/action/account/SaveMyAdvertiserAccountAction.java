package com.foros.action.account;

import com.foros.model.account.AdvertiserAccount;

public class SaveMyAdvertiserAccountAction extends SaveAdvertiserAccountActionBase {

    public SaveMyAdvertiserAccountAction() {
        account = new AdvertiserAccount();
    }

    @Override
    public AdvertiserAccount getExistingAccount() {
        if (account.getId() == null) {
            account.setId(accountService.getMyAccount().getId());
        }
        return super.getExistingAccount();
    }

    public String update() {
        if (getExistingAccount().isSelfServiceFlag()) {
            prepareTnsObjects();
        }

        if (getExistingAccount().isStandalone()) {
            accountService.updateStandaloneAdvertiserAccount(account);
        } else {
            accountService.updateExternalAccount(account);
        }

        return SUCCESS;
    }
}
