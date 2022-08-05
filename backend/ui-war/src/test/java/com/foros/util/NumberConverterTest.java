package com.foros.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkJUnit4TestCase;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.foros.framework.conversion.NumberConverter;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class NumberConverterTest extends XWorkJUnit4TestCase {
    @Test
    @Category(Unit.class)
    public void convertToNumber() {
        assertEquals(new BigDecimal("33000000.4435"), convertToNumber("33,000,000.4435", BigDecimal.class, Locale.UK));
        assertEquals(BigInteger.valueOf(33000000L), convertToNumber("33,000,000", BigInteger.class, Locale.UK));
        assertEquals(new Long(14582), convertToNumber("14,582", Long.class, Locale.UK));
        assertEquals(new Double(140582.68548), convertToNumber("140,582.68548", Double.class, Locale.UK));
        assertEquals(new Float(384500.00541), convertToNumber("384500.00541", Float.class, Locale.UK));

        try {
            convertToNumber("588,666.55,88", BigDecimal.class, Locale.UK);
            fail("Invalid number, conversion must throw exception");
        } catch (TypeConversionException ex) {
        }

        try {
            convertToNumber("588,666.55'88", BigDecimal.class, Locale.UK);
            fail("Invalid number, conversion must throw exception");
        } catch (TypeConversionException ex) {
        }

        try {
            convertToNumber("1E9999999999999", BigDecimal.class, Locale.UK);
            fail("Invalid number: exponent is not allowed, conversion must throw exception");
        } catch (TypeConversionException e) {
        }

        assertNull(convertToNumber("", Double.class, Locale.UK));
    }

    @Test
    @Category(Unit.class)
    public void convertToString() {
        assertEquals("1.23456", convertToString(BigDecimal.valueOf(1.23456), Locale.UK));
    }

    private Map<String, Object> getContext(Locale loc) {
        Map<String, Object> context = new HashMap<String, Object>();
        Map appMap = new HashMap();
        context.put(ActionContext.APPLICATION, appMap);
        context.put(ActionContext.LOCALE, loc);
        return context;
    }

    private <N extends Number> N convertToNumber(String s, Class<N> toClass, Locale loc) {
        NumberConverter nc = container.inject(NumberConverter.class);
        //noinspection unchecked
        return (N)nc.convertFromString(getContext(loc), s, toClass);
    }

    private String convertToString(Object o, Locale loc) {
        NumberConverter nc = container.inject(NumberConverter.class);
        return nc.convertToString(getContext(loc), o);
    }
}
