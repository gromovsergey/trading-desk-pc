package com.foros.web.taglib;

import com.foros.model.site.Tag;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.admin.CurrencyConverter;
import com.foros.util.CurrencyHelper;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.TagPricingUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class NumberFormatter {

    public NumberFormatter() {
    }

    public static String formatNumber(Object value) {
        return formatNumber(toBigDecimal(value), -1);
    }

    public static String formatNumber(Object value, int precision) {
        return formatNumber(toBigDecimal(value), precision);
    }

    public static String formatNumber(BigDecimal value, int precision) {
        if (value == null) {
            return null;
        }

        Locale locale = CurrentUserSettingsHolder.getLocale();

        DecimalFormat nf = NumberUtil.getFormat(locale, precision);

        return nf.format(value.setScale(nf.getMaximumFractionDigits(), RoundingMode.HALF_UP));
    }

    public static String formatCurrency(Object value, String currencyCode) {
        return formatCurrency(toBigDecimal(value), currencyCode, -1);
    }

    public static String formatCurrency(Object value, String currencyCode, int precision) {
        return formatCurrency(toBigDecimal(value), currencyCode, precision);
    }

    public static String formatCurrency(BigDecimal value, String currencyCode, int precision) {
        if (value == null) {
            return null;
        }

        Locale locale = CurrentUserSettingsHolder.getLocale();

        DecimalFormat nf = NumberUtil.getCurrencyFormat(locale, currencyCode, precision);

        return nf.format(value.setScale(nf.getMaximumFractionDigits(), RoundingMode.HALF_UP));
    }

    public static String formatAndConvertIntoCurrency(BigDecimal value, CurrencyConverter converter) {
        if (value == null) {
            return null;
        }
        return formatCurrency(converter.convertFromBase(value), converter.getTarget().getCurrencyCode(), -1);
    }

    public static String formatAndConvertIntoCurrency(BigDecimal value, Long currencyId, CurrencyConverter converter) {
        if (value == null) {
            return null;
        }
        return formatCurrency(converter.convert(currencyId, value), converter.getTarget().getCurrencyCode(), -1);
    }

    /*
     * This method will accept String value and check if that value is ZERO
     * If values is Zero then retunr true, else return false.
     *
     */
    public static boolean isZeroOrNull(String value) throws ParseException {
        if (StringUtil.isPropertyEmpty(value)) {
            return true;
        }

        BigDecimal decimalVal = parseBigDecimal(value);

        return NumberUtil.isZeroOrNull(decimalVal);
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        } if (value instanceof Number) {
            return NumberUtil.toBigDecimal((Number) value);
        } else if (value instanceof String) {
            return parseBigDecimal(((String) value).trim());
        }
        return null;
    }

    private static BigDecimal parseBigDecimal(String s) {
        if (StringUtil.isPropertyEmpty(s)) {
            return null;
        }
        try {
            DecimalFormat nf = NumberUtil.getFormat(CurrentUserSettingsHolder.getLocale());
            nf.setParseBigDecimal(true);
            return (BigDecimal) nf.parse(s);
        } catch (ParseException e) {
            throw new NumberFormatException(s);
        }
    }

    public static String formatTagPricings(Tag tag) throws ParseException {
        if (tag == null) {
            return null;
        }
        return TagPricingUtil.formatTagPricings(tag);
    }
    
    public static String currencySymbol(String code) {
        return CurrencyHelper.getCurrencySymbol(code);
    }
}
