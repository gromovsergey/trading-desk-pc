package com.foros.framework.conversion;

import com.foros.util.DateHelper;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter extends AbstractDateConverter {
    @Override
    protected Date parseDate(String value, TimeZone timeZone, Locale locale) throws ParseException {
        return DateHelper.parseDate(value, timeZone, locale);
    }

    @Override
    protected String formatDate(Date value, TimeZone timeZone, Locale locale) {
        return DateHelper.formatDate(value, timeZone, locale);
    }
}
