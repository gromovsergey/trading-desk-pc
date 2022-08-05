package com.foros.action.account;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.util.StringUtil;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;

public class EditAccountActionBase<T extends Account> extends EditSaveAccountActionBase<T> implements RequestContextsAware {
    @ReadOnly
    public String changeCountry() {
        if (StringUtil.isPropertyNotEmpty(account.getCountry().getCountryCode())) {
            Country country = countryService.find(account.getCountry().getCountryCode());

            account.setCurrency(country.getCurrency());
            account.setTimezone(country.getTimezone());
        } else {
            account.setCurrency(currencyService.getDefault());
            account.setTimezone(null);
        }

        return SUCCESS;
    }

    @Override
    public T getExistingAccount() {
        return account;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (account.getId() == null) {
            return;
        }

        ContextBase context = contexts.getContext(account.getRole());

        if (context != null) {
            context.switchTo(account.getId());
        }
    }

    protected void prepareFlagsForEdit() {
        setInternationalFlag(account.isInternational());
        setTestFl(account.getTestFlag());
        setCmpContactShowPhoneFlag(account.isCmpContactShowPhone());
        setPubAdvertisingReportFlag(account.isPubAdvertisingReportFlag());
        setReferrerReportFlag(account.isReferrerReportFlag());
        setPubConversionReportFlag(account.isPubConversionReportFlag());
        setSiteTargetingFlag(account.isSiteTargetingFlag());
        setSelfServiceFlag(account.isSelfServiceFlag());
    }
}
