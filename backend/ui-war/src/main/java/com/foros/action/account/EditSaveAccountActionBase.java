package com.foros.action.account;

import com.foros.action.campaign.DateTimeBean;
import com.foros.cache.NamedCO;
import com.foros.cache.application.CountryCO;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.ExternalAccount;
import com.foros.model.security.AccountType;
import com.foros.model.security.TextAdservingMode;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.session.security.AccountTO;
import com.foros.session.security.UserService;
import com.foros.util.*;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.messages.MessageProvider;

import javax.ejb.EJB;
import java.util.*;

public abstract class EditSaveAccountActionBase<T extends Account> extends AccountActionBase<T> {
    @EJB
    protected CurrencyService currencyService;

    @EJB
    protected AccountTypeService accountTypeService;

    @EJB
    protected UserService userService;

    private List<User> accountUsers;

    private List<NamedCO<Long>> currencies;
    private List<AccountType> accountTypes;
    private List<NamedCO<Long>> timeZones;
    private List<CountryCO> countries;
    private List<EntityTO> internalUsers;
    private List<AccountTO> internalAccounts;

    private boolean internationalFlag;
    private boolean testFl;
    private boolean cmpContactShowPhoneFlag;
    private boolean pubAdvertisingReportFlag;
    private boolean referrerReportFlag;
    private boolean pubConversionReportFlag;
    private boolean siteTargetingFlag;
    private boolean selfServiceFlag;
    private DateTimeBean selectedContractDate = new DateTimeBean();

    public abstract T getExistingAccount();

    public boolean isInternationalFlag() {
        return internationalFlag;
    }

    public void setInternationalFlag(boolean value) {
        internationalFlag = value;
    }

    public boolean isTestFl() {
        return testFl;
    }

    public void setTestFl(boolean value) {
        testFl = value;
    }

    public boolean isCmpContactShowPhoneFlag() {
        return cmpContactShowPhoneFlag;
    }

    public void setCmpContactShowPhoneFlag(boolean cmpContactShowPhoneFlag) {
        this.cmpContactShowPhoneFlag = cmpContactShowPhoneFlag;
    }

    public boolean isPubAdvertisingReportFlag() {
        return pubAdvertisingReportFlag;
    }

    public void setPubAdvertisingReportFlag(boolean pubAdvertisingReportFlag) {
        this.pubAdvertisingReportFlag = pubAdvertisingReportFlag;
    }

    public boolean isPubConversionReportFlag() {
        return pubConversionReportFlag;
    }

    public void setPubConversionReportFlag(boolean pubConversionReportFlag) {
        this.pubConversionReportFlag = pubConversionReportFlag;
    }

    public boolean isSiteTargetingFlag() {
        return siteTargetingFlag;
    }

    public void setSiteTargetingFlag(boolean siteTargetingFlag) {
        this.siteTargetingFlag = siteTargetingFlag;
    }

    public boolean isReferrerReportFlag() {
        return referrerReportFlag;
    }

    public void setReferrerReportFlag(boolean referrerReportFlag) {
        this.referrerReportFlag = referrerReportFlag;
    }

    public boolean isSelfServiceFlag() {
        return selfServiceFlag;
    }

    public void setSelfServiceFlag(boolean selfServiceFlag) {
        this.selfServiceFlag = selfServiceFlag;
    }

    public List<User> getAccountUsers() {
        if (accountUsers != null) {
            return accountUsers;
        }

        accountUsers = accountService.findAccountUsers(account.getId());
        Collections.sort(accountUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return StringUtil.compareToIgnoreCase(o1.getFullName(), o2.getFullName());
            }
        });

        return accountUsers;
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

    public List<AccountType> getAccountTypes() {
        if (accountTypes != null) {
            return accountTypes;
        }

        accountTypes = accountTypeService.findByRole(account.getRole().getName());

        return accountTypes;
    }

    public List<NamedCO<Long>> getTimeZones() {
        if (timeZones != null) {
            return timeZones;
        }

        timeZones = TimezoneHelper.sort(accountService.getTimeZoneIndex(),
                        MessageProvider.createMessageProviderAdapter());

        for (NamedCO<Long> timeZone : timeZones) {
            timeZone.setName(MessageHelper.prepareMessageKey(timeZone.getName()));
        }

        return timeZones;
    }

    public List<CountryCO> getCountries() {
        if (countries != null) {
            return countries;
        }

        Collection<CountryCO> countryIndex = countryService.getIndex();

        countries = CountryHelper.sort(countryIndex);

        return countries;
    }

    public List<EntityTO> getInternalUsers() {
        if (!(account instanceof ExternalAccount)) {
            return Collections.emptyList();
        }

        if (internalUsers != null) {
            return internalUsers;
        }

        ExternalAccount externalAccount = (ExternalAccount) account;

        if (externalAccount.getInternalAccount() != null && externalAccount.getInternalAccount().getId() != null) {
            if (userService.getMyUser().getRole().isAccountManager()) {
                EntityTO user = userService.getUser(SecurityContext.getPrincipal().getUserId());
                internalUsers = new ArrayList<EntityTO>(1);
                internalUsers.add(user);
            } else {
                internalUsers = userService.getAccountManagers(externalAccount.getInternalAccount().getId(), account.getRole());
                Collections.sort(internalUsers, new IdNameComparator());
            }

            EntityUtils.applyStatusRules(internalUsers,
                    externalAccount.getAccountManager() == null ? null : externalAccount.getAccountManager().getId());
        } else {
            internalUsers = Collections.emptyList();
        }

        return internalUsers;
    }

    public List<AccountTO> getInternalAccounts() {
        if (!(account instanceof ExternalAccount)) {
            return Collections.emptyList();
        }

        if (internalAccounts != null) {
            return internalAccounts;
        }

        ExternalAccount externalAccount = (ExternalAccount) account;

        if (userService.getMyUser().getRole().isAccountManager()) {
            internalAccounts = new ArrayList<AccountTO>(1);
            Account currentInternalAccount = accountService.getMyAccount();
            internalAccounts.add(new AccountTO(
                    currentInternalAccount.getId(),
                    currentInternalAccount.getName(),
                    currentInternalAccount.getStatus().getLetter(),
                    currentInternalAccount.getFlags()));
        } else {
            internalAccounts = accountService.search(AccountRole.INTERNAL);
        }

        EntityUtils.applyStatusRules(internalAccounts,
                externalAccount.getInternalAccount() == null ? null : externalAccount.getInternalAccount().getId());

        return internalAccounts;
    }

    public List<TextAdservingMode> getTextAdservingModes() {
        if (!(account instanceof AdvertisingAccountBase)) {
            return null;
        }

        return TextAdservingMode.getTextAdservingModes(getExistingAccount() instanceof AgencyAccount);
    }

    public boolean isCommissionPresent() {
        AdvertisingAccountBase existingAccount = (AdvertisingAccountBase) getExistingAccount();
        if (!existingAccount.isFinancialFieldsPresent()) {
            return false;
        }

        return existingAccount instanceof AgencyAccount || ((AdvertiserAccount) existingAccount).isInAgencyAdvertiser();
    }

    public boolean isBudgetLimitPresent() {
        return ((AdvertisingAccountBase) getExistingAccount()).isFinancialFieldsPresent();
    }

    public DateTimeBean getSelectedContractDate() {
        return selectedContractDate;
    }
}
