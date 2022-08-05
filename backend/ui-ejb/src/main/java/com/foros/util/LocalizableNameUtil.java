package com.foros.util;

import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.EntityTO;
import com.foros.util.i18n.LocalizationUtil;

import java.util.Comparator;
import java.util.Locale;

public class LocalizableNameUtil {

    /**
     * Locale priority:
     * <ul>
     * <li>Locale of current user</li>
     * <li>Default locale</li>
     * </ul>
     *
     * @param ln name to localize
     * @return localized value, or null if ln is null.
     */
    public static String getLocalizedValue(LocalizableName ln) {
        return getLocalizedValue(ln, false);
    }

    public static String getLocalizedValue(LocalizableName ln, Status status) {
        return EntityUtils.appendStatusSuffix(getLocalizedValue(ln, false), status);
    }

    public static String getLocalizedValue(EntityTO entity) {
        String localizedName = getLocalizedValue(entity.getLocalizableName());

        return EntityUtils.appendStatusSuffix(localizedName, entity.getStatus());
    }

    /**
     * Overloaded version
     *
     * @param ln
     * @param emptyDefaultValue if true, default label will be empty, otherwise default label will be non-empty string
     * @return
     * @see #getLocalizedValue(com.foros.model.LocalizableName)
     */
    public static String getLocalizedValue(LocalizableName ln, boolean emptyDefaultValue) {
        return (ln != null ? getLocalizedValue(ln.getResourceKey(), ln.getDefaultName(), emptyDefaultValue, LocalizationUtil.getCurrentLocale()) : null);
    }

    static String getLocalizedValue(LocalizableName ln, Locale locale) {
        return getLocalizedValue(ln.getResourceKey(), ln.getDefaultName(), locale);
    }

    private static String getLocalizedValue(String key, String defaultValue, Locale locale) {
        return getLocalizedValue(key, defaultValue, false, locale);
    }

    private static String getLocalizedValue(String key, String defaultValue, boolean emptyDefaultValue, Locale locale) {
        if (StringUtil.isPropertyEmpty(key)) {
            return defaultValue;
        }

        if (StringUtil.isPropertyEmpty(defaultValue)) {
            return StringUtil.getLocalizedString(key, locale, emptyDefaultValue);
        } else {
            return StringUtil.getLocalizedStringWithDefault(key, defaultValue, locale);
        }
    }

    public static Comparator<LocalizableName> getComparator() {
        return new LocalizableNameComparator(LocalizationUtil.getCurrentLocale());
    }

    private static class LocalizableNameComparator implements Comparator<LocalizableName> {
        private Locale locale;

        private LocalizableNameComparator(Locale locale) {
            this.locale = locale;
        }

        public int compare(LocalizableName o1, LocalizableName o2) {
            if (o1 == null || o2 == null) {
                throw new NullPointerException("Can't compare null names");
            }

            String val1 = getLocalizedValue(o1, locale);
            String val2 = getLocalizedValue(o2, locale);
            return StringUtil.lexicalCompare(val1, val2);
        }
    }

    public static boolean isActiveLocale(String locale) {
        Locale userLocale = CurrentUserSettingsHolder.getLocale();
        return userLocale != null && locale.equalsIgnoreCase(userLocale.getLanguage());
    }
}
