package com.foros.session.campaign;

import com.foros.session.EntityTO;
import com.foros.session.NamedTO;

public class ISPColocationTO extends EntityTO {
    private NamedTO account;

    public ISPColocationTO(Long id, String name, char status, Long accountId, String accountName) {
        super(id, name, status);
        this.account = new NamedTO(accountId, accountName);
    }

    public NamedTO getAccount() {
        return account;
    }

    public void setAccount(NamedTO account) {
        this.account = account;
    }

    public String getFullName() {
        return account.getName() + " / " + getName();
    }
}
