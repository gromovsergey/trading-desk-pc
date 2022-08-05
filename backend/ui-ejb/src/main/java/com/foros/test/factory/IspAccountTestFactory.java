package com.foros.test.factory;

import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.IspAccount;
import com.foros.model.security.AccountType;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class IspAccountTestFactory extends ExternalAccountTestFactory<IspAccount> {
    @Override
    public AccountsPayableFinancialSettings createFinancialSettings(IspAccount account) {
        AccountsPayableFinancialSettings data = new AccountsPayableFinancialSettings();
        data.setAccountId(account.getId());
        data.setAccount(account);
        return data;
    }

    @Override
    public void persist(IspAccount account) {
        super.persist(account);
        createPersistentFinancialSettings(account);
    }

    @Override
    public IspAccount createPersistent() {
        IspAccount account = create();
        persist(account);
        return account;
    }

    public IspAccount createPersistent(AccountType accountType) {
        IspAccount account = create(accountType);
        persist(account);
        return account;
    }

    public AccountsPayableFinancialSettings createPersistentFinancialSettings(IspAccount account) {
        persistFinancialSettings(account);
        return account.getFinancialSettings();
    }

    @Override
    protected AccountType createAccountType() {
        return ispAccountTypeTF.createPersistent();
    }

    @Override
    protected IspAccount doCreateAccount() {
        return new IspAccount();
    }
}
