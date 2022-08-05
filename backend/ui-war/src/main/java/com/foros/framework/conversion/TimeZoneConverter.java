package com.foros.framework.conversion;

import java.util.Map;
import java.util.TimeZone;

public class TimeZoneConverter extends SingleValueBaseTypeConverter {

    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        return TimeZone.getTimeZone(value);
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        return o.toString();
    }

}
