package com.foros.test.factory;

import com.foros.model.Country;
import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.security.AccountType;
import com.foros.model.security.PaymentMethod;

import java.math.BigDecimal;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class PublisherAccountTestFactory extends ExternalAccountTestFactory<PublisherAccount> {
    @Override
    public AccountsPayableFinancialSettings createFinancialSettings(PublisherAccount account) {
        Country bankCountry = countryTF.findOrCreatePersistent("US");
        AccountsPayableFinancialSettings data = new AccountsPayableFinancialSettings();
        data.setAccountId(account.getId());
        data.setAccount(account);
        data.setCommission(new BigDecimal(0.9));
        data.setBankCountry(bankCountry);
        data.setBankCurrency(bankCountry.getCurrency());
        if ("GB".equals(bankCountry.getCountryCode())) {
            data.setPaymentMethod(PaymentMethod.BACS);
            data.setBankSortCode("SO");
            data.setBankBicCode(null);
            data.setBankAccountIban(null);
        } else {
            data.setPaymentMethod(PaymentMethod.Swift);
            data.setBankSortCode(null);
            data.setBankBicCode("testBankBicCode");
            data.setBankAccountIban("testBankAccountIban");
        }
        data.setBankAccountName("testBankAccountName");
        data.setBankName("testBankName");
        data.setBankBranchName("testBankBranchName");
        data.setBankAccountNumber("testAccountNumber");
        data.setBankAccountType("testBankAccountType");
        return data;
    }

    @Override
    public void persist(PublisherAccount account) {
        super.persist(account);
        createPersistentFinancialSettings(account);
    }

    @Override
    public PublisherAccount createPersistent() {
        PublisherAccount account = create();
        persist(account);
        return account;
    }

    public PublisherAccount createPersistent(AccountType accountType) {
        PublisherAccount account = create(accountType);
        persist(account);
        return account;
    }

    public AccountsPayableFinancialSettings createPersistentFinancialSettings(PublisherAccount account) {
        persistFinancialSettings(account);
        return account.getFinancialSettings();
    }

    @Override
    protected AccountType createAccountType() {
       return publisherAccountTypeTF.createPersistent();
    }

    @Override
    protected PublisherAccount doCreateAccount() {
        PublisherAccount publisherAccount = new PublisherAccount();
        publisherAccount.setPassbackBelowFold(false);
        return publisherAccount;
    }

    @Override
    public void populate(PublisherAccount account, InternalAccount internalAccount) {
        super.populate(account, internalAccount);
        account.setCreativesReapproval(false);
    }
}
