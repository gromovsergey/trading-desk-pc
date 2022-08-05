package com.foros.framework.conversion;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.TypeConverter;
import java.lang.reflect.Member;
import java.util.Locale;
import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

public class LocalDateTimeConverter implements TypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (value instanceof String && LocalDateTime.class.isAssignableFrom(toType)) {
            Locale locale = (Locale) context.get(ActionContext.LOCALE);
            String date = (String) value;
            String pattern = DateTimeFormat.patternForStyle("SS", locale);
            return DateTimeFormat.forPattern(pattern).parseDateTime(date).toLocalDateTime();
        }

        return NO_CONVERSION_POSSIBLE;
    }
}
