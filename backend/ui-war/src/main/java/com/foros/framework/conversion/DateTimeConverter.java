package com.foros.framework.conversion;

import com.foros.util.DateHelper;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeConverter extends AbstractDateConverter {

    @Override
    protected Date parseDate(String value, TimeZone timeZone, Locale locale) throws ParseException {
        return DateHelper.parseDateTime(value, timeZone, locale);
    }

    @Override
    protected String formatDate(Date value, TimeZone timeZone, Locale locale) {
        return DateHelper.formatDateTime(value, timeZone, locale);
    }
}
