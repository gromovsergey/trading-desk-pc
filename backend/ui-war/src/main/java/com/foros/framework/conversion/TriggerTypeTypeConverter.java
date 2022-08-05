package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.foros.model.channel.trigger.TriggerType;

import java.util.Map;

public class TriggerTypeTypeConverter extends SingleValueBaseTypeConverter {
    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        try {
            return TriggerType.byString(value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof TriggerType)) {
            throw new TypeConversionException("Object " + o + " is not a TriggerType");
        }

        return Character.valueOf(((TriggerType) o).getLetter()).toString();
    }
}
