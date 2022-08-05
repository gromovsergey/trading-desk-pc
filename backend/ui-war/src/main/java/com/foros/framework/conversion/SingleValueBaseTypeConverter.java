package com.foros.framework.conversion;

import com.foros.util.StringUtil;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.opensymphony.xwork2.conversion.TypeConverter;
import java.lang.reflect.Member;
import java.util.Map;

public abstract class SingleValueBaseTypeConverter implements TypeConverter {
    public abstract Object convertFromString(Map<String, Object> context, String value, Class toClass);

    public abstract String convertToString(Map<String, Object> context, Object o);

    @Override
    public final Object convertValue(Map<String, Object> context, Object target, Member member,
                                     String propertyName, Object value, Class toType) {
        return convertValue(context, value, toType);
    }

    private Object convertValue(Map<String, Object> context, Object o, Class toClass) {
        if (o == null) {
            return toClass == String.class ? "" : null;
        }
        try {
            if (toClass.equals(String.class)) {
                return convertToString(context, o);
            } else if (o instanceof String[]) {
                return convertFromString(context, (String[])o, toClass);
            } else if (o instanceof String) {
                return convertFromString(context, (String)o, toClass);
            } else if (o instanceof Character) {
                return convertFromString(context, o.toString(), toClass);
            } else {
                throw new TypeConversionException("It is impossible to convert from " + o.getClass() + " to " + toClass);
            }
        } catch (TypeConversionException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new TypeConversionException(e);
        }
    }

    private Object convertFromString(Map<String, Object> context, String[] values, Class toClass) {
        if (values == null || values.length < 1 || StringUtil.isPropertyEmpty(values[0])) {
            return null;
        }
        if (values.length > 1) {
            throw new TypeConversionException("Too many values for convert (" + values.length + " but expected only 1)");
        }
        return convertFromString(context, values[0], toClass);
    }
}
