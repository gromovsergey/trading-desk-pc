package com.foros.model.account;

import javax.persistence.Entity;

@Entity
public abstract class AccountsPayableAccountBase extends ExternalAccount<AccountsPayableFinancialSettings> {

    public AccountsPayableAccountBase() {
        setFinancialSettings(new AccountsPayableFinancialSettings());
    }

    public AccountsPayableAccountBase(Long accountId) {
        super(accountId);
        setFinancialSettings(new AccountsPayableFinancialSettings());
    }

    public AccountsPayableAccountBase(Long id, String name) {
        super(id, name);
        setFinancialSettings(new AccountsPayableFinancialSettings());
    }
}
