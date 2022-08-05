package com.foros.test.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.security.AccountAddress;
import com.foros.model.security.AccountType;

import java.math.BigDecimal;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class AdvertiserAccountTestFactory extends ExternalAccountTestFactory<AdvertiserAccount> {
    @Override
    public AdvertisingFinancialSettings createFinancialSettings(AdvertiserAccount account) {
        AdvertisingFinancialSettings data = new AdvertisingFinancialSettings();
        data.setAccountId(account.getId());
        data.setAccount(account);
        data.setCreditLimit(BigDecimal.TEN);
        data.getData().setPrepaidAmount(BigDecimal.TEN);
        return data;
    }

    public AdvertiserAccount createAdvertiserInAgency(AgencyAccount agency) {
        AdvertiserAccount advertiser = new AdvertiserAccount();
        advertiser.setName(getTestEntityRandomName());
        advertiser.setLegalName(advertiser.getName());
        advertiser.setAgency(agency);
        AccountAddress address = new AccountAddress();
        address.setLine1("testLine");
        address.setCity("testCity");
        advertiser.setBillingAddress(address);
        advertiser.setLegalAddress(address);
        return advertiser;
    }

    @Override
    public void persist(AdvertiserAccount account) {
        super.persist(account);
        createPersistentFinancialSettings(account);
        entityManager.flush();
    }

    public void persistAgencyAdvertiser(AdvertiserAccount advertiser) {
        accountService.addAdvertiser(advertiser);
    }

    public void updateAgencyAdvertiser(AdvertiserAccount advertiser) {
        accountService.updateAdvertiser(advertiser);
    }

    @Override
    public AdvertiserAccount createPersistent() {
        AccountType accountType = advertiserAccountTypeTF.createPersistent();
        AdvertiserAccount account = create(accountType);
        persist(account);
        return account;
    }

    public AdvertiserAccount createPersistent(AccountType accountType) {
        AdvertiserAccount account = create(accountType);
        persist(account);
        return account;
    }

    public AdvertiserAccount createPersistentAdvertiserInAgency(AgencyAccount agency) {
        AdvertiserAccount advertiser = createAdvertiserInAgency(agency);
        persistAgencyAdvertiser(advertiser);
        return advertiser;
    }

    public AdvertisingFinancialSettings createPersistentFinancialSettings(AdvertiserAccount account) {
        persistFinancialSettings(account);
        return account.getFinancialSettings();
    }

    @Override
    protected AccountType createAccountType() {
        return advertiserAccountTypeTF.createPersistent();
    }

    @Override
    protected AdvertiserAccount doCreateAccount() {
        return new AdvertiserAccount();
    }
}
