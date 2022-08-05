package com.foros.framework.conversion;

import com.foros.framework.conversion.SingleValueBaseTypeConverter;
import com.foros.model.campaign.RateType;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import java.util.Map;

public class RateTypeConverter extends SingleValueBaseTypeConverter {
    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        try {
            return RateType.valueOf(Integer.valueOf(value));
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof RateType)) {
            throw new TypeConversionException("Object o is not RateType");
        }

        return String.valueOf(((RateType)o).ordinal());
    }
}
