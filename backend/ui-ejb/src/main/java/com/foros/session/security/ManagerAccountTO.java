package com.foros.session.security;

import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.model.account.Account;

public class ManagerAccountTO extends EntityTO {
    private AccountRole role;
    private Long flags;

    public ManagerAccountTO(Long id, String name, char status, AccountRole role, Long flags) {
        super(id, name, status);

        this.role  = role;
        this.flags = (flags == null ? 0L : flags);
    }

    public AccountRole getRole() {
        return role;
    }

    public Long getFlags() {
        return flags;
    }

    public boolean getTestFlag() {
        return (flags & Account.TEST_FLAG) != 0;
    }
}
