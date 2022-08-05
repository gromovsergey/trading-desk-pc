package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.cache.NamedCO;
import com.foros.cache.application.CountryCO;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.finance.AccountsPayableFinanceService;
import com.foros.util.CountryHelper;
import com.foros.util.CurrencyHelper;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;

public abstract class EditSaveAccountsPayableFinanceActionBase extends BaseActionSupport
        implements ModelDriven<AccountsPayableFinancialSettings>, RequestContextsAware {

    @EJB
    protected CurrencyService currencyService;

    @EJB
    protected CountryService countryService;

    @EJB
    protected AccountService accountService;

    @EJB
    protected AccountsPayableFinanceService accountsPayableFinanceService;

    protected AccountsPayableFinancialSettings financialSettings;

    private List<NamedCO<Long>> currencies;
    private List<CountryCO> countries;
    private List<EntityTO> accountBillToUsers;

    public abstract AccountsPayableAccountBase getExistingAccount();

    public EditSaveAccountsPayableFinanceActionBase() {
        financialSettings = new AccountsPayableFinancialSettings();
    }

    public List<NamedCO<Long>> getCurrencies() {
        if (currencies != null) {
            return currencies;
        }

        Collection<NamedCO<Long>> currencyIndex = currencyService.getIndex();
        currencies = new ArrayList<NamedCO<Long>>(currencyIndex);

        Collections.sort(currencies, CurrencyHelper.getCurrencyComparator());

        return currencies;
    }

    public List<CountryCO> getCountries() {
        if (countries != null) {
            return countries;
        }

        Collection<CountryCO> countryIndex = countryService.getIndex();

        countries = CountryHelper.sort(countryIndex);

        return countries;
    }

    public List<EntityTO> getAccountBillToUsers() {
        if (accountBillToUsers != null) {
            return accountBillToUsers;
        }

        accountBillToUsers = accountService.getAccountUsers(financialSettings.getAccountId());

        EntityUtils.applyStatusRules(accountBillToUsers,
                financialSettings.getDefaultBillToUser() == null ? null : financialSettings.getDefaultBillToUser().getId());

        return accountBillToUsers;
    }

    @Override
    public AccountsPayableFinancialSettings getModel() {
        return financialSettings;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        Account account = accountService.find(financialSettings.getAccountId());
        contexts.switchTo(account);
    }
}
