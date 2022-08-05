package com.foros.framework.conversion;

import com.foros.security.currentuser.CurrentUserSettingsHolder;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.TypeConversionException;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class AbstractDateConverter extends SingleValueBaseTypeConverter {

    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        try {
            Locale locale = (Locale) context.get(ActionContext.LOCALE);
            TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();
            Date result = parseDate(value, timeZone, locale);
            if (result.getClass().equals(toClass)) {
                return result;
            } else {
                long time = result.getTime();
                // Try to create an instance of date object using <init>(long time) constructor if any
                return toClass.getConstructor(long.class).newInstance(time);
            }
        } catch (Exception ex) {
            throw new TypeConversionException(ex);
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof Date)) {
            throw new TypeConversionException("Couldn't convert object: " + o.toString() + " to string as Date");
        }

        Locale locale = (Locale) context.get(ActionContext.LOCALE);
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();

        return formatDate((Date) o, timeZone, locale);
    }

    protected abstract Date parseDate(String value, TimeZone timeZone, Locale locale) throws ParseException;

    protected abstract String formatDate(Date value, TimeZone timeZone, Locale locale);
}
