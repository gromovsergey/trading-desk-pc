package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.IspAccount;

public class ViewISPAccountAction extends ViewAccountActionBase<IspAccount> {
    private Long id;

    @ReadOnly
    public String view() {
        account = accountService.viewIspAccount(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
