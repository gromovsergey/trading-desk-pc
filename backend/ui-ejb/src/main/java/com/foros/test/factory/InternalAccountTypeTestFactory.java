package com.foros.test.factory;

import com.foros.model.security.AccountType;
import com.foros.security.AccountRole;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class InternalAccountTypeTestFactory extends AccountTypeTestFactory {
    @Override
    public AccountType create() {
        AccountType accountType = new AccountType();
        accountType.setAccountRole(AccountRole.INTERNAL);
        accountType.setChannelCheck(false);

        populate(accountType);

        return accountType;
    }

    @Override
    public AccountType createPersistent() {
        AccountType type = create();
        persist(type);
        return type;
    }

    public AccountType findAny() {
        AccountType accountType = findAny(AccountType.class, new QueryParam("accountRole", AccountRole.INTERNAL));
        return accountType;
    }
}
