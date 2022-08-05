package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import java.math.BigDecimal;
import java.util.Map;

public class IntegerNumberFormattingConverter extends NumberConverter {

    @Override
    public Object convertFromString(Map<String, Object> context, String s, Class toClass) {
        BigDecimal res = (BigDecimal) super.convertFromString(context, s, BigDecimal.class);
        try {
            return res.intValueExact();
        } catch (ArithmeticException e) {
            throw new TypeConversionException("Unparseable formatted integer: \"" + s + "\"");
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        return getNumberFormat(getLocale(context)).format(o);
    }

}
