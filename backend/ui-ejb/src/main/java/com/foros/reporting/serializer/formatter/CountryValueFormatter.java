package com.foros.reporting.serializer.formatter;

import java.util.Locale;

import com.foros.util.StringUtil;

public class CountryValueFormatter extends ValueFormatterSupport<String> {

    @Override
    public String formatText(String value, FormatterContext context) {
        if (value == null || value.length() < 1) {
            return "";
        }

        Locale locale = context.getLocale();
        String localizedString = StringUtil.getLocalizedString("global.country." + value.trim() + ".name", locale, true);
        if (StringUtil.isPropertyEmpty(localizedString)) {
            return value;
        }
        return localizedString;
    }
}