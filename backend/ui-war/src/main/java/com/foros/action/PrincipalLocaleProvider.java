package com.foros.action;

import com.opensymphony.xwork2.LocaleProvider;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import java.util.Locale;

public class PrincipalLocaleProvider implements LocaleProvider {

    @Override
    public Locale getLocale() {
        return CurrentUserSettingsHolder.getLocale();
    }

}
