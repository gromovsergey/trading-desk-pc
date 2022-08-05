package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.IspSelfIdAware;
import com.foros.model.account.IspAccount;
import com.foros.restriction.annotation.Restrict;

public class EditMyISPAccountAction extends EditAccountActionBase<IspAccount> implements IspSelfIdAware {

    public EditMyISPAccountAction() {
        account = new IspAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = (IspAccount) accountService.getMyAccount();

        return SUCCESS;
    }

    @Override
    public void setIspId(Long ispId) {
        account.setId(ispId);
    }
}
