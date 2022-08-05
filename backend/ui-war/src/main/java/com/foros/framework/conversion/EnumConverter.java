package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import java.util.Map;

public class EnumConverter extends SingleValueBaseTypeConverter {
    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        try {
            return Enum.valueOf(toClass, value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof Enum)) {
            throw new TypeConversionException("Object " + o + " is not an Enum");
        }

        return ((Enum)o).name();
    }
}
