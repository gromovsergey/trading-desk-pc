package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.foros.model.channel.trigger.TriggerChannelType;

import java.util.Map;

public class TriggerChannelTypeTypeConverter extends SingleValueBaseTypeConverter {
    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        try {
            return TriggerChannelType.valueOfString(value);
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof TriggerChannelType)) {
            throw new TypeConversionException("Object " + o + " is not a TriggerChannelType");
        }

        return ((TriggerChannelType) o).getLetter();
    }
}
