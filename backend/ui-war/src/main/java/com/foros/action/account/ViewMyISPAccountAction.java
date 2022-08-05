package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.IspAccount;
import com.foros.restriction.annotation.Restrict;

public class ViewMyISPAccountAction extends ViewAccountActionBase<IspAccount> {

    @ReadOnly
    @Restrict(restriction="Account.view", parameters="'ISP'")
    public String view() {
        account = (IspAccount) accountService.getMyAccountWithTerms();

        return SUCCESS;
    }
}
