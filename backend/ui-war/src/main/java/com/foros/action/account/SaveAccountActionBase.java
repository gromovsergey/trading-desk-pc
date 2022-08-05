package com.foros.action.account;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.FlagsUtil;
import com.foros.util.context.RequestContexts;

import java.text.ParseException;
import java.util.Locale;
import java.util.TimeZone;

public class SaveAccountActionBase<T extends Account> extends EditSaveAccountActionBase<T> implements RequestContextsAware {
    private T existingAccount;

    @Override
    public T getExistingAccount() {
        if (account.getId() == null) {
            return account;
        }

        if (existingAccount != null) {
            return existingAccount;
        }

        existingAccount = (T) accountService.find(account.getId());

        return existingAccount;
    }

    protected void prepareFlagsForSave() {
        boolean internationalFlagChanged = !getExistingAccount().isInternational() && isInternationalFlag();
        boolean testFlagChanged = !getExistingAccount().isTestFlag() && isTestFl();
        boolean cmpShowPhoneFlagChanged = getExistingAccount().isCmpContactShowPhone() != isCmpContactShowPhoneFlag();
        boolean pubAdvertisingReportFlagChanged = getExistingAccount().isPubAdvertisingReportFlag() != isPubAdvertisingReportFlag();
        boolean referrerReportFlagChanged = getExistingAccount().isReferrerReportFlag() != isReferrerReportFlag();
        boolean pubConversionReportFlagChanged = getExistingAccount().isPubConversionReportFlag() != isPubConversionReportFlag();
        boolean siteTargetingFlagChanged = getExistingAccount().isSiteTargetingFlag() != isSiteTargetingFlag();
        boolean selfServiceFlagChanged = getExistingAccount().isSelfServiceFlag() != isSelfServiceFlag();

        if (!internationalFlagChanged && !testFlagChanged && !cmpShowPhoneFlagChanged && !pubAdvertisingReportFlagChanged && !referrerReportFlagChanged
                && !pubConversionReportFlagChanged && !siteTargetingFlagChanged && !selfServiceFlagChanged) {
            account.unregisterChange("flags");
        }

        if (internationalFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.INTERNATIONAL, isInternationalFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.INTERNATIONAL, getExistingAccount().isInternational()));
        }

        if (testFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.TEST_FLAG, isTestFl()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.TEST_FLAG, getExistingAccount().isTestFlag()));
        }

        if (cmpShowPhoneFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.CMP_CONTACT_SHOW_PHONE, isCmpContactShowPhoneFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.CMP_CONTACT_SHOW_PHONE, getExistingAccount().isCmpContactShowPhone()));
        }

        if (pubAdvertisingReportFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.PUB_ADVERTISING_REPORT_FLAG, isPubAdvertisingReportFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.PUB_ADVERTISING_REPORT_FLAG, getExistingAccount().isPubAdvertisingReportFlag()));
        }

        if (referrerReportFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.REFERRER_REPORT_FLAG, isReferrerReportFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.REFERRER_REPORT_FLAG, getExistingAccount().isReferrerReportFlag()));
        }

        if (pubConversionReportFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.PUB_CONVERSION_REPORT_FLAG, isPubConversionReportFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.PUB_CONVERSION_REPORT_FLAG, getExistingAccount().isPubConversionReportFlag()));
        }

        if (siteTargetingFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.SITE_TARGETING_FLAG, isSiteTargetingFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.SITE_TARGETING_FLAG, getExistingAccount().isSiteTargetingFlag()));
        }

        if (selfServiceFlagChanged) {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.SELF_SERVICE_FLAG, isSelfServiceFlag()));
        } else {
            account.setFlags(FlagsUtil.set(account.getFlags(), Account.SELF_SERVICE_FLAG, getExistingAccount().isSelfServiceFlag()));
        }

        // Repopulate flag fields values (needed to show correct values if there were errors)
        setInternationalFlag(account.isInternational());
        setTestFl(account.getTestFlag());
        setCmpContactShowPhoneFlag(account.isCmpContactShowPhone());
        setPubAdvertisingReportFlag(account.isPubAdvertisingReportFlag());
        setReferrerReportFlag(account.isReferrerReportFlag());
        setPubConversionReportFlag(account.isPubConversionReportFlag());
        setSiteTargetingFlag(account.isSiteTargetingFlag());
        setSelfServiceFlag(account.isSelfServiceFlag());
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        T existingAccount = getExistingAccount();
        if (existingAccount.getId() != null) {
            contexts.switchTo(existingAccount);
        }
    }

    protected void prepareFinancialSettings(AdvertisingAccountBase account) {
        AdvertisingFinancialSettings existingSettings = ((AdvertisingAccountBase) getExistingAccount()).getFinancialSettings();
        AdvertisingFinancialSettings modifiedSettings = account.getFinancialSettings();

        transferChanges(modifiedSettings, existingSettings);
        existingSettings.setVersion(modifiedSettings.getVersion());

        account.setFinancialSettings(existingSettings);
    }

    protected void prepareContractDate(AdvertisingAccountBase account) {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        try {
            account.setContractDate(getSelectedContractDate().getDate(getAccountTimeZone(), locale));
        } catch (ParseException e) {
            addFieldError("contractDate", getText("errors.field.date"));
        }
    }

    private TimeZone getAccountTimeZone() {
        if (account.getTimezone() != null) {
            return account.getTimezone().toTimeZone();
        }

        return getExistingAccount().getTimezone().toTimeZone();
    }

    protected void prepareInitialFinancialSettings(AdvertisingAccountBase account) {
        AdvertisingFinancialSettings initialSettings = new AdvertisingFinancialSettings();
        initialSettings.setAccount(account);
        initialSettings.unregisterChange("account");

        transferChanges(account.getFinancialSettings(), initialSettings);

        account.setFinancialSettings(initialSettings);
    }

    private void transferChanges(AdvertisingFinancialSettings from, AdvertisingFinancialSettings to) {
        if (from.isChanged("commission", "commissionPercent")) {
            to.setCommission(from.getCommission());
        }
        if (from.getData().isChanged("prepaidAmount")) {
            to.getData().setPrepaidAmount(from.getData().getPrepaidAmount());
        }
    }
}
