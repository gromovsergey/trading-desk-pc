package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.model.account.PublisherAccount;

public class ViewPublisherAccountAction extends ViewAccountActionBase<PublisherAccount> {
    private Long id;

    @ReadOnly
    public String view() {
        account = accountService.viewPublisherAccount(id);

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
