package com.foros.rs.sandbox.factory;

import com.foros.model.account.AgencyAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.security.AccountType;
import com.foros.test.factory.AgencyAccountTestFactory;

public class AgencyAccountGeneratorFactory extends BaseGeneratorFactory<AgencyAccount, AgencyAccountTestFactory> {
    private AccountType agencyAccountType;
    private  InternalAccount internalAccount;

    public AgencyAccountGeneratorFactory(AgencyAccountTestFactory accountTestFactory, AccountType agencyAccountType, InternalAccount internalAccount) {
        super(accountTestFactory);
        this.agencyAccountType = agencyAccountType;
        this.internalAccount = internalAccount;
    }

    @Override
    protected AgencyAccount createDefault() {
        AgencyAccount account = factory.create(agencyAccountType, internalAccount);
        String name = account.getName();
        factory.populate(account);
        account.setName(name);

        account.setInternational(true);
        return account;
    }
}

