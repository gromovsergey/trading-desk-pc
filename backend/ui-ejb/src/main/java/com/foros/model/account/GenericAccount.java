package com.foros.model.account;

import com.foros.security.AccountRole;

public class GenericAccount extends Account {
    private AccountRole role;

    @Override
    public AccountRole getRole() {
        return role;
    }

    @Override
    public boolean isInternational() {
        throw new UnsupportedOperationException();
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }
}
