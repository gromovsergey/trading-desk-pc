package com.foros.session.reporting;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.util.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;

public class ReportHelper {

    public static final String MONTH_TO_DATE = "monthtodate";
    public static final String WEEK_TO_DATE = "weektodate";
    public static final String MONTH = "month";
    public static final String WEEK = "week";
    public static final String DAY = "day";
    public static final String LESS_THAN_MONTH = "lessthanmonth";
    public static final String N_A = "n/a";

    public static String getRangeType(Date dateFrom, Date dateTo, boolean withWeek) {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        if (dateFrom.equals(dateTo)) {
            return DAY;
        }

        Calendar c1 = Calendar.getInstance(timeZone, locale);
        Calendar c2 = Calendar.getInstance(timeZone, locale);

        c1.setTime(dateFrom);
        c2.setTime(dateTo);

        if (withWeek) {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                long daysBetween = TimeUnit.DAYS.convert(dateTo.getTime() - dateFrom.getTime(), TimeUnit.MILLISECONDS);
                if (daysBetween == 6) {
                    return WEEK;
                }
                if (daysBetween < 6) {
                    return WEEK_TO_DATE;
                }
            }
        }

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR) || c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH) || c1.get(Calendar.DAY_OF_MONTH) != 1) {
            return N_A;
        }

        if (c2.get(Calendar.DAY_OF_MONTH) == c2.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            return MONTH;
        }

        return MONTH_TO_DATE;
    }

    public static String getRangeType(String dateFromStr, String dateToStr, boolean withWeek) {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        Date dateFrom, dateTo;

        try {
            dateFrom = DateHelper.parseDate(dateFromStr, DateFormat.SHORT, timeZone, locale);
            dateTo = DateHelper.parseDate(dateToStr, DateFormat.SHORT, timeZone, locale);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }

        return getRangeType(dateFrom, dateTo, withWeek);
    }

    public static List<String> getRangeTypes(String dateFromStr, String dateToStr){
        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        Date dateFrom, dateTo;
        List<String> rangeTypes = new ArrayList<String>();

        try {
            dateFrom = DateHelper.parseDate(dateFromStr, DateFormat.SHORT, timeZone, locale);
            dateTo = DateHelper.parseDate(dateToStr, DateFormat.SHORT, timeZone, locale);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }

        if (dateFrom.equals(dateTo)) {
            rangeTypes.add(DAY);
        }

        Calendar from = Calendar.getInstance(timeZone);
        Calendar to = Calendar.getInstance(timeZone);
        // week always starts on monday in the isp report and AdServer
        from.setFirstDayOfWeek(Calendar.MONDAY);
        to.setFirstDayOfWeek(Calendar.MONDAY);

        from.setTime(dateFrom);
        to.setTime(dateTo);

        if (isLessThanMonth(new DateTime(dateFrom), new DateTime(dateTo))) {
            rangeTypes.add(LESS_THAN_MONTH);
        }
        
        if (from.get(Calendar.YEAR) == from.get(Calendar.YEAR) && from.get(Calendar.MONTH) == to.get(Calendar.MONTH) &&
                from.get(Calendar.DAY_OF_MONTH) == 1) {
            rangeTypes.add(MONTH_TO_DATE);
            if (to.get(Calendar.DAY_OF_MONTH) == to.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                rangeTypes.add(MONTH);
            }
        }

        if (from.get(Calendar.DAY_OF_WEEK) == from.getFirstDayOfWeek()) {
            if (from.get(Calendar.WEEK_OF_YEAR) == to.get(Calendar.WEEK_OF_YEAR)) {
                rangeTypes.add(WEEK_TO_DATE);
                if (to.get(Calendar.DAY_OF_WEEK) == to.getActualMaximum(Calendar.DAY_OF_WEEK)) {
                    rangeTypes.add(WEEK);
                }
            }
        }

        if (rangeTypes.isEmpty()) {
            rangeTypes.add(N_A);
        }
        return rangeTypes;

    }

    public static boolean isLessThanMonth(ReadableInstant startDate, ReadableInstant endDate) {
        Days d = Days.daysBetween(startDate, endDate);
        return d.getDays() <= 30;
    }
}
