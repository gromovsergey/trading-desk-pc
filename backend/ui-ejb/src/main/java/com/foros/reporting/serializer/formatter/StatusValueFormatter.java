package com.foros.reporting.serializer.formatter;

import com.foros.model.Status;
import com.foros.util.StringUtil;

import java.util.Locale;

public class StatusValueFormatter extends ValueFormatterSupport<Status> {
    @Override
    public String formatText(Status value, FormatterContext context) {
        if (value == null) {
            return "";
        }

        Locale locale = context.getLocale();
        return StringUtil.getLocalizedString("enums.Status." + value.name(), locale);
    }
}