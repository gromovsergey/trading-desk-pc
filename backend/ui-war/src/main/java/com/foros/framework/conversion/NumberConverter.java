package com.foros.framework.conversion;

import com.foros.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter;
import com.opensymphony.xwork2.inject.Inject;

public class NumberConverter extends SingleValueBaseTypeConverter {
    private static final Pattern LETTERS = Pattern.compile("\\p{Alpha}+");

    private TypeConverter defaultConverter;

    protected Locale getLocale(Map context) {
        return (Locale) context.get(ActionContext.LOCALE);
    }

    @Inject
    public void setDefaultConverter(XWorkBasicConverter defaultConverter) {
        this.defaultConverter = defaultConverter;
    }

    @Override
    public Object convertFromString(Map<String, Object> context, String s, Class toClass) {
        Locale loc = getLocale(context);
        if (loc == null || toClass != BigDecimal.class && toClass != BigInteger.class) {
            return defaultConverter.convertValue(context, null, null, null, s, toClass);
        }

        String value = s.trim();
        Matcher m = LETTERS.matcher(value);
        if (m.find()) {
            throw new TypeConversionException("Unparseable number: letters are not allowed");
        }

        DecimalFormat nf = getNumberFormat(loc);

        if (nf.getDecimalFormatSymbols().getGroupingSeparator() == '\u00A0') {
            value = StringUtil.spaceToNbsp(value);
        }

        ParsePosition parsePos = new ParsePosition(0);
        BigDecimal res = (BigDecimal) nf.parse(value, parsePos);

        if (parsePos.getIndex() != value.length()) {
            throw new TypeConversionException("Unparseable number: \"" + value + "\" at position " + parsePos.getIndex());
        }

        if (toClass == BigDecimal.class) {
            return res.stripTrailingZeros();
        } else {
            return res.toBigInteger();
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!((o instanceof BigDecimal) || (o instanceof BigInteger) || (o instanceof Long))) {
            return (String) defaultConverter.convertValue(context, null, null, null, o, String.class);
        }

        final NumberFormat nf = getNumberFormat(getLocale(context));

        if (o instanceof BigDecimal) {
            nf.setMaximumFractionDigits(((BigDecimal) o).scale());
        }
        return nf.format(o);
    }

    protected DecimalFormat getNumberFormat(Locale locale) {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        nf.setGroupingUsed(true);
        nf.setParseBigDecimal(true);
        return nf;
    }
}
