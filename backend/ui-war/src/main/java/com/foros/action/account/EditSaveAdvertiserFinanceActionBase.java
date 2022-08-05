package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;

public abstract class EditSaveAdvertiserFinanceActionBase extends BaseActionSupport implements ModelDriven<AdvertisingFinancialSettings>, RequestContextsAware {

    @EJB
    protected AccountService accountService;

    @EJB
    protected AdvertisingFinanceService advertisingFinanceService;

    protected AdvertisingFinancialSettings financialSettings = new AdvertisingFinancialSettings();

    protected Boolean allowEmptyAccountBillToUser;

    private List<EntityTO> accountBillToUsers;

    public abstract AdvertisingAccountBase getExistingAccount();

    public boolean isAgencyFlag() {
        return getExistingAccount() instanceof AgencyAccount;
    }

    public boolean isAgencyAdvertiserAccountRequest() {
        return !getExistingAccount().isStandalone();
    }

    public char getDecimalSeparator() {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        return ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
    }

    public char getThousandSeparator() {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        return ((DecimalFormat)numberFormat).getDecimalFormatSymbols().getGroupingSeparator();
    }

    public List<EntityTO> getAccountBillToUsers() {
        if (accountBillToUsers != null) {
            return accountBillToUsers;
        }

        Long accountId = financialSettings.getAccountId();

        AdvertiserAccount advertiser = accountService.findAdvertiserAccount(accountId);
        if (advertiser.getAgency() != null) {
            accountId = advertiser.getAgency().getId();
        }

        accountBillToUsers = accountService.getAccountUsers(accountId);

        EntityUtils.applyStatusRules(accountBillToUsers,
                financialSettings.getDefaultBillToUser() == null ? null : financialSettings.getDefaultBillToUser().getId());

        return accountBillToUsers;
    }

    @Override
    public AdvertisingFinancialSettings getModel() {
        return financialSettings;
    }

    public boolean isAllowEmptyAccountBillToUser() {
        if (allowEmptyAccountBillToUser == null) {
            AdvertisingFinancialSettings existingFinancialSettings = advertisingFinanceService.getFinancialSettings(financialSettings.getAccountId());
            allowEmptyAccountBillToUser = existingFinancialSettings.getDefaultBillToUser() == null;
        }
                
        return allowEmptyAccountBillToUser;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        Account account = accountService.find(financialSettings.getAccountId());
        contexts.switchTo(account);
    }
}
