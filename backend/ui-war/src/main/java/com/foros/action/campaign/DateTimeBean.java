package com.foros.action.campaign;

import com.foros.util.DateHelper;
import com.foros.util.StringUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeBean {
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

    public Date getDate(TimeZone timeZone, Locale locale) throws ParseException {
        return timePart == null ? DateHelper.parseDate(getDateTime(), DATE_STYLE, timeZone, locale) :
                DateHelper.parseDateTime(getDateTime(), DATE_STYLE, DATE_STYLE, timeZone, locale);
    }
    
    public boolean getIsEmpty() {
        return StringUtil.isPropertyEmpty(datePart) || StringUtil.isPropertyEmpty(getDateTime());
    }

    private String getDateTime() {
        return datePart + (timePart != null ? " " + timePart : "");
    }

    /**
     * @return {@link #getDateTime()}
     */
    @Override
    public String toString() {
        return getDateTime();
    }
}
