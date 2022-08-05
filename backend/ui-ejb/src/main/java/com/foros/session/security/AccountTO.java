package com.foros.session.security;

import com.foros.model.account.Account;
import com.foros.security.AccountRole;
import com.foros.session.DisplayStatusEntityTO;

public class AccountTO extends DisplayStatusEntityTO {
    private AccountRole role;
    private String countryCode;
    protected long flags;

    public AccountTO(Long id, String name, char status, long flags) {
        super(id, name, status, null);

        this.flags = flags;
    }

    public AccountTO() {
    }

    public AccountTO(Long id, String name, char status, AccountRole role, String countryCode, Long displayStatusId, long flags) {
        super(id, name, status, Account.getDisplayStatus(displayStatusId));

        this.role = role;
        this.countryCode = countryCode;
        this.flags = flags;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isTestFlag() {
        return (flags & Account.TEST_FLAG) == 1;
    }
}
