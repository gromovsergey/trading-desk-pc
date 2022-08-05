package com.foros.rs.sandbox.factory;

import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.security.AccountType;
import com.foros.test.factory.IspAccountTestFactory;

public class IspAccountGeneratorFactory extends BaseGeneratorFactory<IspAccount, IspAccountTestFactory> {
    private AccountType accountType;
    private InternalAccount internalAccount;

    public IspAccountGeneratorFactory(IspAccountTestFactory accountTestFactory, AccountType accountType, InternalAccount internalAccount) {
        super(accountTestFactory);
        this.accountType = accountType;
        this.internalAccount = internalAccount;
    }

    @Override
    protected IspAccount createDefault() {
        IspAccount account = factory.create(accountType, internalAccount);
        return account;
    }
}

