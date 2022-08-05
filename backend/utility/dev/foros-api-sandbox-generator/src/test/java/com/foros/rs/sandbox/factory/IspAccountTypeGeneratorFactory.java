package com.foros.rs.sandbox.factory;

import com.foros.model.security.AccountType;
import com.foros.test.factory.IspAccountTypeTestFactory;

public class IspAccountTypeGeneratorFactory extends BaseGeneratorFactory<AccountType, IspAccountTypeTestFactory> {

    public IspAccountTypeGeneratorFactory(IspAccountTypeTestFactory factory) {
        super(factory);
    }

    @Override
    protected AccountType createDefault() {
        AccountType accountType = factory.create();
        accountType.setAdvancedReportsFlag(true);
        return accountType;
    }
}
