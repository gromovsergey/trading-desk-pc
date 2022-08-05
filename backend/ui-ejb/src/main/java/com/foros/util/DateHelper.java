package com.foros.util;

import com.foros.security.currentuser.CurrentUserSettingsHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateHelper {
    public static final String SHORT_STYLE = "S-";

    private static final String[] propMonthNames = new String[] {
            "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"
    };

    private DateHelper() {
    }

    public static Date parseDate(String value, int style, TimeZone timeZone, Locale locale) throws ParseException {
        if (value == null || "".equals(value.trim())) {
            return null;
        }

        DateFormat dateFormat = DateFormat.getDateInstance(style, locale);

        dateFormat.setLenient(false);
        dateFormat.setTimeZone(timeZone);

        return dateFormat.parse(value);
    }

    public static Date parseDate(String value, TimeZone timeZone, Locale locale) throws ParseException {
        return parseDate(value, DateFormat.SHORT, timeZone, locale);
    }

    public static Date parseTime(String value, int style, TimeZone timeZone, Locale locale) throws ParseException {
        if (value == null || "".equals(value.trim())) {
            return null;
        }

        DateFormat dateFormat = DateFormat.getTimeInstance(style, locale);

        dateFormat.setLenient(false);

        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }


        ParsePosition pos = new ParsePosition(0);
        Date res = dateFormat.parse(value, pos);

        if (pos.getIndex() != value.length()) {
            throw new ParseException("Parse error", pos.getIndex());
        }

        return res;
    }

    public static String formatDate(Date date, int style, TimeZone timeZone, Locale locale) {
        if (date == null) {
            return "";
        }

        DateFormat dateFormat = DateFormat.getDateInstance(style, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public static String formatDate(Date date, TimeZone timeZone, Locale locale) {
        return formatDate(date, DateFormat.SHORT, timeZone, locale);
    }

    public static String formatTime(Date time, int style, TimeZone timeZone, Locale locale) {
        if (time == null) {
            return "";
        }

        DateFormat dateFormat = DateFormat.getTimeInstance(style, locale);

        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        return dateFormat.format(time);
    }

    public static String formatDateTime(Date date, int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        if (date == null) {
            return "";
        }

        DateFormat dateFormat = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public static String formatDateTime(Date date, TimeZone timeZone, Locale locale) {
        return formatDateTime(date, DateFormat.SHORT, DateFormat.SHORT, timeZone, locale);
    }

    public static String formatDateTimeTimeZone(Date date, TimeZone timeZone, Locale locale) {
        return formatDateTimeTimeZone(date, DateFormat.SHORT, DateFormat.SHORT, timeZone, locale);
    }

    private static String formatDateTimeTimeZone(Date date, int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        return formatDateTime(date, dateStyle, timeStyle, timeZone, locale) + " " + timeZone.getID();
    }

    public static Date parseDateTime(String value,
                                     int dateStyle,
                                     int timeStyle,
                                     TimeZone timeZone,
                                     Locale locale) throws ParseException {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        value = value.trim();

        DateFormat dateFormat = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);

        dateFormat.setLenient(false);
        dateFormat.setTimeZone(timeZone);

        ParsePosition pos = new ParsePosition(0);
        Date res = dateFormat.parse(value, pos);

        if (pos.getIndex() != value.length()) {
            throw new ParseException("Parse error", pos.getIndex());
        }

        return res;
    }

    public static Date parseDateTime(String value,
                                     TimeZone timeZone,
                                     Locale locale) throws ParseException {
        return parseDateTime(value, DateFormat.SHORT, DateFormat.SHORT, timeZone, locale);
    }

    public static Date parseDateTimeTimeZone(String value, Locale locale) throws ParseException {
        return parseDateTimeTimeZone(value, DateFormat.SHORT, DateFormat.SHORT, locale);
    }

    private static Date parseDateTimeTimeZone(String value,
                                             int dateStyle,
                                             int timeStyle,
                                             Locale locale) throws ParseException {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] dateTimeTimeZone = splitDateTimeTimeZoneString(value);
        return parseDateTime(dateTimeTimeZone[0], dateStyle, timeStyle, TimeZone.getTimeZone(dateTimeTimeZone[1]), locale);
    }

    private static String[] splitDateTimeTimeZoneString(String value) throws ParseException {
        value = value.trim();

        int lastSpacePos = value.lastIndexOf(' ');
        if (lastSpacePos < 0) {
            throw new ParseException("Parse error", 0);
        }

        return new String[] {value.substring(0, lastSpacePos), value.substring(lastSpacePos + 1)};
    }

    public static Date add(Date date, int years, int months, int days, int hours, int minutes, int seconds, int milliseconds) {
        Date result = date;
        if (date != null) {
            Calendar toCalendar = new GregorianCalendar();
            toCalendar.setTime(date);
            toCalendar.add(Calendar.YEAR, years);
            toCalendar.add(Calendar.MONTH, months);
            toCalendar.add(Calendar.DAY_OF_MONTH, days);
            toCalendar.add(Calendar.HOUR, hours);
            toCalendar.add(Calendar.MINUTE, minutes);
            toCalendar.add(Calendar.SECOND, seconds);
            toCalendar.add(Calendar.MILLISECOND, milliseconds);
            result = toCalendar.getTime();
        }

        return result;
    }

    public static Date clearTime(Date date, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date createFirstDateOfMonth(int year, int month, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, month - 1, 1, 0, 0, 0);
        return calendar.getTime();
    }

    public static Date createLastDateOfMonth(int year, int month, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);

        calendar.set(year, month - 1, 1, 0, 0, 0);
        int lastDay = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);

        return calendar.getTime();
    }

    public static Date createTime(int hours, int minutes, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String getLocalizedMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);

        return getLocalizedMonth(month);
    }

    public static String getLocalizedMonth(int month) {
        return StringUtil.getLocalizedString("report.input.field.monthAndYear." + propMonthNames[month]);
    }

    public static String formatTimeIntervalLong(Long timeInterval) {
        DSTimeInterval interval = new DSTimeInterval(timeInterval);

        if (interval.getDays() == 0 && interval.getHours() == 0 && interval.getMinutes() == 0 && interval.getSeconds() == 0) {
            return "0 " + getLocalizedIntervalString(0, "interval.second");
        }

        StringBuilder res = new StringBuilder();

        if (interval.getDays() > 0) {
            res.append(interval.getDays());
            res.append(" ");
            res.append(getLocalizedIntervalString(interval.getDays(), "interval.day"));
            res.append(" ");
        }
        if (interval.getHours() > 0) {
            res.append(interval.getHours());
            res.append(" ");
            res.append(getLocalizedIntervalString(interval.getHours(), "interval.hour"));
            res.append(" ");
        }
        if (interval.getMinutes() > 0) {
            res.append(interval.getMinutes());
            res.append(" ");
            res.append(getLocalizedIntervalString(interval.getMinutes(), "interval.minute"));
            res.append(" ");
        }
        if (interval.getSeconds() > 0) {
            res.append(interval.getSeconds());
            res.append(" ");
            res.append(getLocalizedIntervalString(interval.getSeconds(), "interval.second"));
            res.append(" ");
        }

        return res.toString().trim();
    }

    private static String getLocalizedIntervalString(int count, String intervalName) {
        String s = "";
        if (count != 1) {
            s = "s";
        }
        return StringUtil.getLocalizedString(intervalName + s);
    }

    public static String formatTimeInterval(DSTimeInterval interval) {
        if (interval == null) {
            return "";
        }

        StringBuilder key = new StringBuilder("timeInterval");
        List<Integer> args = new LinkedList<Integer>();

        if (interval.getDays() == 0 && interval.getHours() == 0 && interval.getMinutes() == 0) {
            return StringUtil.getLocalizedString("timeIntervalLess1Min");
        }

        if (interval.getDays() > 0) {
            key.append('D');
            args.add(interval.getDays());
        }

        if (interval.getHours() > 0) {
            key.append('H');
            args.add(interval.getHours());
        }

        if (interval.getMinutes() > 0) {
            key.append('M');
            args.add(interval.getMinutes());
        }

        String strInterval = StringUtil.getLocalizedString(key.toString(), args.toArray());

        if (interval.isNegative()) {
            strInterval = "-" + strInterval;
        }

        return strInterval;
    }

    public static long getDateTimeZoneOffset(TimeZone accountTimeZone) {
        Calendar defaultTimeZoneCalendar = Calendar.getInstance();
        Calendar accountTimeZoneCalendar = Calendar.getInstance(accountTimeZone);

        Date now = new Date();
        defaultTimeZoneCalendar.setTime(now);
        accountTimeZoneCalendar.setTime(now);

        long accountDayOfYear = accountTimeZoneCalendar.get(Calendar.DAY_OF_YEAR);
        long defaultDayOfYear = defaultTimeZoneCalendar.get(Calendar.DAY_OF_YEAR);

        int accountYear = accountTimeZoneCalendar.get(Calendar.YEAR);

        int defaultYear = defaultTimeZoneCalendar.get(Calendar.YEAR);

        if (accountYear == defaultYear) {
            return accountDayOfYear - defaultDayOfYear;
        }

        if (accountYear < defaultYear) {
            return accountDayOfYear - (defaultDayOfYear + accountTimeZoneCalendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        }

        return accountDayOfYear + defaultTimeZoneCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) - defaultDayOfYear;

    }

    public static String getRelativeDate(Date date, TimeZone timeZone) {
        if (date == null) {
            return StringUtil.getLocalizedString("notAvailable");
        }

        Date nowDay = clearTime(new Date(), timeZone);
        Date dateDay = clearTime(date, timeZone);

        long millisDiff = nowDay.getTime() - dateDay.getTime();
        long diff = millisDiff / (1000 * 60 * 60 * 24);

        if (diff == 0) {
            return StringUtil.getLocalizedString("relativeDate.today");
        } else if (diff == 1) {
            return StringUtil.getLocalizedString("relativeDate.yesterday");
        } else if (diff > 0) {
            return StringUtil.getLocalizedString("relativeDate.daysAgo", diff);
        }

        return StringUtil.getLocalizedString("notAvailable");
    }

    public static String formatDateTimeLong(long time) {
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();
        Calendar c = Calendar.getInstance(timeZone, locale);
        c.setTimeInMillis(time);
        String strDateTime = formatDateTime(c.getTime(), DateFormat.SHORT, DateFormat.SHORT, timeZone, locale);
        return strDateTime;
    }

    /**
     * @param time HH:mm in GMT
     * @return time in user's
     */
    public static String formatTimeString(String time) {
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();

        Date date = parseTime(time, timeZone);
        return formatTime(date, DateFormat.SHORT, timeZone, locale);
    }

    /**
     * @return time format pattern in user's locale
     */
    public static String getTimeFormatPattern() {
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, CurrentUserSettingsHolder.getLocale());
        return ((SimpleDateFormat) dateFormat).toPattern();
    }

    private static Date parseTime(String time, TimeZone tz) {
        try {
            DateFormat df = new SimpleDateFormat("HH:mm");
            df.setTimeZone(tz);
            return df.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatLocalDate(LocalDate localDate, Locale locale) {
        return localDate.toString(getFormatter(locale));
    }

    public static LocalDate parseLocalDate(String str, Locale locale) {
        return getFormatter(locale).parseDateTime(str).toLocalDate();
    }

    public static LocalDateTime parseLocalDateTime(String str, Locale locale) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        try {
            return LocalDateTime.fromDateFields(format.parse(str));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DateTimeFormatter getFormatter(Locale locale) {
        String pattern = DateTimeFormat.patternForStyle(SHORT_STYLE, locale);
        return DateTimeFormat.forPattern(pattern);
    }

    public static String getAmpms() {
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, CurrentUserSettingsHolder.getLocale());
        String[] ampms = ((SimpleDateFormat) dateFormat).getDateFormatSymbols().getAmPmStrings();
        return "{\"am\":\"" + ampms[0] + "\",\"pm\":\"" + ampms[1] + "\"}";
    }

    public static LocalDate fullMonthBack(LocalDate date) {
        return date.withDayOfMonth(1).minusMonths(1);
    }

    public static LocalDate yesterday() {
        return yesterday(TimeZone.getDefault());
    }

    public static LocalDate yesterday(TimeZone timeZone) {
        return new LocalDate(DateTimeZone.forTimeZone(timeZone)).minusDays(1);
    }

    public static LocalDate thisMonthBegin(TimeZone timeZone) {
        return new LocalDate(DateTimeZone.forTimeZone(timeZone)).withDayOfMonth(1);
    }

    public static LocalDate thisMonthEnd(TimeZone timeZone) {
        return thisMonthBegin(timeZone).plusMonths(1).minusDays(1);
    }
}
