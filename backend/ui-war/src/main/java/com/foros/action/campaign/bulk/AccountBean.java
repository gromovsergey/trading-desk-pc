package com.foros.action.campaign.bulk;

import com.foros.action.IdNameBean;
import com.foros.model.account.Account;
import com.foros.security.AccountRole;

public class AccountBean extends IdNameBean {
    private AccountRole role;
    private boolean testFlag;
    private boolean international;
    private String countryCode;

    public AccountBean() {
        super();
    }

    public AccountBean(Account account) {
        this(account.getId().toString(), account.getRole(), account.getName());
    }

    public AccountBean(String id, AccountRole role, String name) {
        super(id, name);

        this.role = role;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public boolean getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(boolean testFlag) {
        this.testFlag = testFlag;
    }

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
