package com.foros.util.formatter;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.DateHelper;

import java.text.ParseException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter implements FieldFormatter<Date> {
    private final int style;

    public DateFormatter(int style) {
        this.style = style;
    }

    public DateFormatter() {
        this(DateFormat.SHORT);
    }

    public int getStyle() {
        return style;
    }

    public String getString(Date date) {
        if (date == null) {
            return "";
        }

        String result;
        try {
            // for all reports we need to use GMT timezone (as the database and application servers)
            // GMT timezone also must be used when the query is building (see CommonReportAction.prepareFilter())
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            Locale locale = CurrentUserSettingsHolder.getLocale();

            result =  DateHelper.formatDate(date, style, timeZone, locale);
         } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date value: " + date, e);
        }
        return result;
    }
    
    public Date parse(String str) throws ParseException {
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();

        return DateHelper.parseDate(str, style, timeZone, locale);
    }
}
