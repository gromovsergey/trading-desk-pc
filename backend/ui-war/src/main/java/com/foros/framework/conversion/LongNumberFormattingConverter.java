package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import java.math.BigDecimal;
import java.util.Map;

public class LongNumberFormattingConverter extends NumberConverter {

    @Override
    public Object convertFromString(Map<String, Object> context, String s, Class toClass) {
        BigDecimal res = (BigDecimal) super.convertFromString(context, s, BigDecimal.class);
        try {
            return res.longValueExact();
        } catch (ArithmeticException e) {
            throw new TypeConversionException("Unparseable formatted long: \"" + s + "\"");
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        return getNumberFormat(getLocale(context)).format(o);
    }

}
