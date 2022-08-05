package com.foros.action.account;

import com.foros.model.account.Account;
import com.foros.model.account.CmpAccount;
import com.foros.util.FlagsUtil;

public class SaveMyCMPAccountAction extends SaveAccountActionBase<CmpAccount> {

    public SaveMyCMPAccountAction() {
        account = new CmpAccount();
    }

    protected void prepareFlagsForSave() {
        account.setFlags(getExistingAccount().getFlags());

        boolean cmpShowPhoneFlagChanged = getExistingAccount().isCmpContactShowPhone() != isCmpContactShowPhoneFlag();

        if (!cmpShowPhoneFlagChanged) {
            // Repopulate flag fields values (needed to show correct values if there were errors)
            setCmpContactShowPhoneFlag(getExistingAccount().isCmpContactShowPhone());

            account.unregisterChange("flags");
        } else {
            if (cmpShowPhoneFlagChanged) {
                account.setFlags(FlagsUtil.set(account.getFlags(), Account.CMP_CONTACT_SHOW_PHONE, isCmpContactShowPhoneFlag()));
            } else {
                account.setFlags(FlagsUtil.set(account.getFlags(), Account.CMP_CONTACT_SHOW_PHONE, getExistingAccount().isCmpContactShowPhone()));
            }

            // Repopulate flag fields values (needed to show correct values if there were errors)
            setCmpContactShowPhoneFlag(account.isCmpContactShowPhone());
        }
    }

    public String update() {
        account.setId(accountService.getMyAccount().getId());

        prepareFlagsForSave();

        accountService.updateExternalAccount(account);

        return SUCCESS;
    }
}
