package com.foros.util;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateRangeSelect {
    private LocalDate fromDate;
    private LocalDate toDate;

    public DateRangeSelect(String fastChangeId, String fromDateStr, String toDateStr) {
        Locale locale = CurrentUserSettingsHolder.getLocale();

        if (StringUtil.isPropertyNotEmpty(fastChangeId) && !"TOT".equals(fastChangeId)) {
            try {
                // date parameters are already in account time zone
                Date from = DateHelper.parseDate(fromDateStr, DateFormat.SHORT, TimeZone.getTimeZone("GMT"), locale);
                Date to = DateHelper.parseDate(toDateStr, DateFormat.SHORT, TimeZone.getTimeZone("GMT"), locale);

                fromDate = new LocalDate(from);
                toDate = new LocalDate(to);
            } catch (ParseException e) {
            }
        }
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
