package com.foros.test.factory;

import com.foros.model.account.AccountFinancialSettings;
import com.foros.model.account.ExternalAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.security.AccountType;
import com.foros.model.security.User;

import javax.ejb.EJB;

public abstract class ExternalAccountTestFactory<T extends ExternalAccount> extends AccountTestFactory<T> {
    @EJB
    protected UserTestFactory userTF;

    @EJB
    protected InternalAccountTestFactory internalAccountTF;

    protected abstract AccountFinancialSettings createFinancialSettings(T account);

    protected abstract T doCreateAccount();

    protected abstract AccountType createAccountType();

    @Override
    public T create() {
        AccountType accountType = createAccountType();
        return create(accountType, null);
    }
    
    public T create(InternalAccount internalAccount) {
        AccountType accountType = createAccountType();
        return create(accountType, internalAccount);
    }

    @Override
    public void persist(T account) {
        accountService.createExternalAccount(account);
        entityManager.flush();
    }

    public void update(T account) {
        accountService.updateExternalAccount(account);
        entityManager.flush();
    }

    public T createPersistent(InternalAccount internalAccount) {
        T result = create(internalAccount);
        persist(result);
        return result;
    }
    
    public T create(AccountType accountType) {
        return create(accountType, null);
    }
    
    public T create(AccountType accountType, InternalAccount internalAccount) {
        T account = doCreateAccount();
        account.setAccountType(accountType);
        populate(account, internalAccount);

        AccountFinancialSettings settings = createFinancialSettings(account);
        account.setFinancialSettings(settings);
        return account;
    }
    
    public void populate(T account, InternalAccount internalAccount) {
        super.populate(account);

        if (internalAccount == null) {
            internalAccount = internalAccountTF.createPersistent();
        }

        account.setInternalAccount(internalAccount);
    }
    
    public void persistFinancialSettings(ExternalAccount account) {
        AccountFinancialSettings settings = account.getFinancialSettings();
        User user = userTF.createPersistent(account);
        settings.setDefaultBillToUser(user);
        entityManager.persist(settings);
    }
}

