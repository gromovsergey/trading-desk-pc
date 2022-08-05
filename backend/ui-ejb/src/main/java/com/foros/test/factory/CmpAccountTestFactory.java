package com.foros.test.factory;

import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.CmpAccount;
import com.foros.model.security.AccountType;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class CmpAccountTestFactory extends ExternalAccountTestFactory<CmpAccount> {
    @Override
    public AccountsPayableFinancialSettings createFinancialSettings(CmpAccount account) {
        AccountsPayableFinancialSettings settings = new AccountsPayableFinancialSettings();
        settings.setAccountId(account.getId());
        settings.setAccount(account);
        return settings;
    }

    @Override
    public CmpAccount createPersistent() {
        CmpAccount account = create();
        persist(account);
        return account;
    }

    @Override
    protected AccountType createAccountType() {
        return cmpAccountTypeTF.createPersistent();
    }

    @Override
    protected CmpAccount doCreateAccount() {
        return new CmpAccount();
    }
}
