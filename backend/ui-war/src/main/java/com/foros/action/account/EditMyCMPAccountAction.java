package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.model.account.CmpAccount;
import com.foros.restriction.annotation.Restrict;

public class EditMyCMPAccountAction extends EditAccountActionBase<CmpAccount> implements CmpSelfIdAware {

    public EditMyCMPAccountAction() {
        account = new CmpAccount();
    }

    @ReadOnly
    @Restrict(restriction="Account.update", parameters="find('Account',#target.model.id)")
    public String edit() {
        account = (CmpAccount) accountService.getMyAccount();

        return SUCCESS;
    }

    @Override
    public void setCmpId(Long cmpId) {
        account.setId(cmpId);
    }
}
