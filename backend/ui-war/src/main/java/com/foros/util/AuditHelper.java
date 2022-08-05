package com.foros.util;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;

import java.util.Locale;

public class AuditHelper {
    public static String toHexString(String value) {
        String hexValue;
        try{
            hexValue = Integer.toHexString(Integer.parseInt(value));
        } catch (Exception e) {
            hexValue = value;
        }
        return hexValue.toUpperCase();
    }

    public static String toTimeString(String value) {
        String timeValue;
        try{
            int val = Integer.parseInt(value);
            timeValue = String.format("%02d:%02d", val / 60, val % 60);
        } catch (Exception e) {
            timeValue = value;
        }
        return timeValue.toUpperCase();
    }

    public static String getLocalizedStringWithDefault(String value, String defaultValue) {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        return StringUtil.getLocalizedStringWithDefault(value, defaultValue, locale);
    }
}
