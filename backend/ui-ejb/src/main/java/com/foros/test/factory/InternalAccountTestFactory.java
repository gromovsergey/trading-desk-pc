package com.foros.test.factory;

import com.foros.model.account.InternalAccount;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class InternalAccountTestFactory extends AccountTestFactory<InternalAccount> {
    @Override
    public InternalAccount create() {
        AccountType accountType = internalAccountTypeTF.createPersistent();
        return create(accountType);
    }

    public InternalAccount create(AccountType accountType) {
        InternalAccount account = new InternalAccount();
        account.setAccountType(accountType);
        populate(account);

        account.setAdvContact(new User());
        account.setPubContact(new User());
        account.setIspContact(new User());
        account.setCmpContact(new User());

        return account;
    }

    @Override
    public void persist(InternalAccount account) {
        accountService.createInternalAccount(account);
        entityManager.flush();
    }

    public void update(InternalAccount account) {
        accountService.updateInternalAccount(account);
        entityManager.flush();
    }

    @Override
    public InternalAccount createPersistent() {
        InternalAccount account = create();
        persist(account);
        return account;
    }

    public InternalAccount createPersistent(AccountType accountType) {
        InternalAccount account = create(accountType);
        persist(account);
        return account;
    }
}
