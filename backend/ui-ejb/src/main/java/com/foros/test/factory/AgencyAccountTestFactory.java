package com.foros.test.factory;

import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.security.AccountType;

import java.math.BigDecimal;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class AgencyAccountTestFactory extends ExternalAccountTestFactory<AgencyAccount> {
    @Override
    public AdvertisingFinancialSettings createFinancialSettings(AgencyAccount account) {
        AdvertisingFinancialSettings data = new AdvertisingFinancialSettings();
        data.setAccountId(account.getId());
        data.setCreditLimit(BigDecimal.TEN);
        data.getData().setPrepaidAmount(BigDecimal.TEN);
        data.setAccount(account);
        return data;
    }

    @Override
    public void persist(AgencyAccount account) {
        super.persist(account);
        createPersistentFinancialSettings(account);
    }

    @Override
    public AgencyAccount createPersistent() {
        AgencyAccount account = create();
        persist(account);
        return account;
    }

    public AgencyAccount createPersistent(AccountType accountType) {
        AgencyAccount account = create(accountType);
        persist(account);
        return account;
    }

    public AdvertisingFinancialSettings createPersistentFinancialSettings(AgencyAccount account) {
        persistFinancialSettings(account);
        return account.getFinancialSettings();
    }

    @Override
    protected AccountType createAccountType() {
        return agencyAccountTypeTF.createPersistent();
    }

    @Override
    protected AgencyAccount doCreateAccount() {
        return new AgencyAccount();
    }
}
