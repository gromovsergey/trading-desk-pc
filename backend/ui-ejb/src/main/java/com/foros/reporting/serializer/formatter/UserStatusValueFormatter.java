package com.foros.reporting.serializer.formatter;

import com.foros.util.StringUtil;

import java.util.Locale;

public class UserStatusValueFormatter extends ValueFormatterSupport<String> {

    @Override
    public String formatText(String value, FormatterContext context) {
        if (value == null || value.length() < 1) {
            return "";
        }

        Locale locale = context.getLocale();
        return StringUtil.getLocalizedString("report.userStatus." + value, locale);
    }
}
