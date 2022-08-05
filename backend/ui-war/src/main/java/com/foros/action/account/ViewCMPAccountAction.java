package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.CmpAccount;

public class ViewCMPAccountAction extends ViewAccountActionBase<CmpAccount> {
    private Long id;

    @ReadOnly
    public String view() {
        account = accountService.viewCmpAccount(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
