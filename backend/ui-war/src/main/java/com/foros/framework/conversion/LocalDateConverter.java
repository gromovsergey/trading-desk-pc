package com.foros.framework.conversion;

import com.foros.util.DateHelper;

import com.opensymphony.xwork2.ActionContext;
import java.util.Locale;
import java.util.Map;
import org.joda.time.LocalDate;

public class LocalDateConverter extends SingleValueBaseTypeConverter {

    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        Locale locale = getLocale(context);
        return DateHelper.parseLocalDate(value, locale);
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        Locale locale = getLocale(context);
        return  DateHelper.formatLocalDate((LocalDate) o, locale);
    }

    private Locale getLocale(Map<String, Object> context) {
        return (Locale) context.get(ActionContext.LOCALE);
    }

}
