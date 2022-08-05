package com.foros.action.account;

import com.foros.model.account.AdvertisingAccountBase;
import com.foros.security.currentuser.CurrentUserSettingsHolder;

import java.util.Locale;

public class EditAdvertisingAccountActionBase<T extends AdvertisingAccountBase> extends EditAccountActionBase<T> {
    protected void prepareContractDateForEdit() {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        if (account.getContractDate() != null) {
            getSelectedContractDate().setDate(account.getContractDate(), account.getTimezone().toTimeZone(), locale);
        }
    }
}
