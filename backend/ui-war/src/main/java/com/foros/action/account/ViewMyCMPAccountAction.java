package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.CmpAccount;
import com.foros.restriction.annotation.Restrict;

public class ViewMyCMPAccountAction extends ViewAccountActionBase<CmpAccount> {

    @ReadOnly
    @Restrict(restriction="Account.view", parameters="'CMP'")
    public String view() {
        account = (CmpAccount) accountService.getMyAccountWithTerms();

        return SUCCESS;
    }
}
