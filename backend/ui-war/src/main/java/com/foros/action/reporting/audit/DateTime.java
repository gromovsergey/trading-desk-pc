package com.foros.action.reporting.audit;

import com.foros.util.DateHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;

public class DateTime {
    private static final int DATE_STYLE = DateFormat.SHORT;

    private String datePart;
    private String timePart;

    public String getDatePart() {
        return datePart;
    }

    public void setDatePart(String datePart) {
        this.datePart = datePart;
    }

    public String getTimePart() {
        return timePart;
    }

    public void setTimePart(String timePart) {
        this.timePart = timePart;
    }

    public void setDate(Date date, TimeZone timeZone, Locale locale) {
        datePart = DateHelper.formatDate(date, DATE_STYLE, timeZone, locale);
        timePart = DateHelper.formatTime(date, DATE_STYLE, timeZone, locale);
    }

    public LocalDateTime getDate(Locale locale) {
        return DateHelper.parseLocalDateTime(getDateTime(), locale);
    }

    public boolean getIsEmpty() {
        return StringUtils.isBlank(datePart) || StringUtils.isBlank(getDateTime());
    }

    private String getDateTime() {
        return datePart + " " + timePart;
    }

    /**
     * @return {@link #getDateTime()}
     */
    @Override
    public String toString() {
        return getDateTime();
    }
}
