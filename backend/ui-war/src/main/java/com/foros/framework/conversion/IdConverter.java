package com.foros.framework.conversion;

import java.util.Map;

/**
 * Converter for <b>entity.id</b> field and for some of long-type fields
 *
 * @author Andrey Chernyshov
 */
public class IdConverter extends SingleValueBaseTypeConverter {

    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        return Long.valueOf(value.trim());
    }

    public String convertToString(Map<String, Object> context, Object o) {
        return o.toString();
    }
}
