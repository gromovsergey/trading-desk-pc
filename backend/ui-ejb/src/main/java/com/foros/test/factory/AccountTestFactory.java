package com.foros.test.factory;

import com.foros.model.Timezone;
import com.foros.model.account.Account;
import com.foros.model.currency.Currency;
import com.foros.model.security.AccountAddress;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public abstract class AccountTestFactory<T extends Account> extends TestFactory<T> {
    @EJB
    protected AccountService accountService;

    @EJB
    protected CurrencyTestFactory currencyTF;

    @EJB
    protected CountryTestFactory countryTF;

    @EJB
    protected AgencyAccountTypeTestFactory agencyAccountTypeTF;

    @EJB
    protected InternalAccountTypeTestFactory internalAccountTypeTF;

    @EJB
    protected IspAccountTypeTestFactory ispAccountTypeTF;

    @EJB
    protected CmpAccountTypeTestFactory cmpAccountTypeTF;

    @EJB
    protected PublisherAccountTypeTestFactory publisherAccountTypeTF;

    @EJB
    protected AdvertiserAccountTypeTestFactory advertiserAccountTypeTF;

    public void populate(Account account) {
        account.setName(getTestEntityRandomName());
        account.setLegalName(account.getName());

        Currency currency = currencyTF.findOrCreatePersistent("GBP");
        account.setCurrency(currency);

        account.setCountry(countryTF.findOrCreatePersistent("US"));

        account.setTimezone(new Timezone(1L, "GMT"));
        account.setBillingAddress(createAddress());
        account.setLegalAddress(createAddress());
    }

    protected AccountAddress createAddress() {
        AccountAddress address = new AccountAddress();

        address.setLine1("line1");
        address.setCity("line2");
        address.setZip("zip");

        return address;
    }

    public void delete(Account account) {
        accountService.delete(account.getId());
    }

    public void undelete(Account account) {
        accountService.undelete(account.getId());
    }
}
