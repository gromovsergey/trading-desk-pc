package com.foros.framework.conversion;

import com.foros.framework.support.TimeZoneAware;
import com.foros.util.DateHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeZoneDateTimeConverter extends AbstractDateConverter {
    @Override
    protected Date parseDate(String value, TimeZone timeZone, Locale locale) throws ParseException {
        return DateHelper.parseDateTimeTimeZone(value, locale);
    }

    @Override
    protected String formatDate(Date value, TimeZone timeZone, Locale locale) {
        TimeZone adjustedTimeZone = adjustTimeZone(timeZone);
        return DateHelper.formatDateTimeTimeZone(value, adjustedTimeZone, locale);
    }

    private TimeZone adjustTimeZone(TimeZone defaultTimeZone) {
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        Object action = invocation.getAction();

        TimeZone adjustedTimeZone = null;
        if (action instanceof TimeZoneAware) {
            adjustedTimeZone = ((TimeZoneAware) action).getTimeZone();
        }
        return adjustedTimeZone != null ? adjustedTimeZone : defaultTimeZone;
    }
}
