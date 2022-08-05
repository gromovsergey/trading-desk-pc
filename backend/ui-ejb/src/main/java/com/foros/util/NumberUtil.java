package com.foros.util;

import com.foros.security.currentuser.CurrentUserSettingsHolder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.Locale;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.LongValidator;

public class NumberUtil {
    private static final String MAX_PRECISION_LIMIT = "999999999";
    private static final String MIN_CURRENCY_LIMIT = "1";
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private NumberUtil() {
    }

    public static boolean isDouble(String value) {
        try {
            parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isDouble(String value, int maxFractionDigit) {
        try {
            parseDouble(value,  maxFractionDigit);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Returns:
     * Throws NumberFormatException when value is empty or not a number with given fraction digit size
     */
    public static double parseDouble(String value, int maxFractionDigit) {
        if (StringUtil.isPropertyEmpty(value)) {
            throw new NumberFormatException();
        }

        Locale locale = CurrentUserSettingsHolder.getLocale();
        ParsePosition pos = new ParsePosition(0);
        NumberFormat nf = NumberFormat.getInstance(locale);
        value = value.trim();
        char decimalSeparator = new DecimalFormatSymbols(locale).getDecimalSeparator();

        if (value.indexOf(decimalSeparator) != -1) {
            value = value.replaceFirst("([^0]+)[0]+$", "$1");
        }

        Number parsedValue = nf.parse(value, pos);
        if (pos.getIndex() != value.length()) {
            throw new NumberFormatException(value + " is not a double");
        }

        if (maxFractionDigit >= 0) {
            NumberFormat inf = NumberFormat.getIntegerInstance(locale);
            pos = new ParsePosition(0);
            inf.parse(value, pos);
            if (value.length() - pos.getIndex() > (maxFractionDigit + 1)) {
                throw new NumberFormatException();
            }
        }

        return parsedValue.doubleValue();
    }

    public static double parseDouble(String value) {
        return parseDouble(value, -1);
    }

    public static int parseInt(String value) {
        try {
            IntegerValidator validator = IntegerValidator.getInstance();
            Locale locale = CurrentUserSettingsHolder.getLocale();
            return validator.validate(value, locale);
        } catch (NullPointerException e) {
            throw new NumberFormatException();
        }
    }

    public static boolean isInt(String value) {
        try {
            parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            return parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isLong(String value) {
        try {
            parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static long parseLong(String value) {
        try {
            LongValidator validator = LongValidator.getInstance();
            Locale locale = CurrentUserSettingsHolder.getLocale();
            return validator.validate(value, locale);
        } catch (NullPointerException e) {
            throw new NumberFormatException();  
        }
    }

    public static long parseLong(String value, long defaultValue) {
        try {
            return parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static BigDecimal parseBigDecimal(String value) throws ParseException {
        Locale locale;
        try {

            locale = CurrentUserSettingsHolder.getLocale();
        } catch (NullPointerException e) {
            throw new NumberFormatException();
        }

        return parseBigDecimal(value, locale);
    }

    private static BigDecimal parseBigDecimal(String value, Locale locale) throws ParseException {
        DecimalFormat nf = NumberUtil.getFormat(locale);

        return parseBigDecimal(value, nf);
    }

    public static BigDecimal parseBigDecimal(String value, DecimalFormat nf) throws ParseException {
        if (StringUtil.isPropertyEmpty(value)) {
            return null;
        }

        ParsePosition pos = new ParsePosition(0);

        nf.setParseBigDecimal(true);
        BigDecimal bigDecimal = (BigDecimal) nf.parse(value, pos);

        if (pos.getIndex() != value.length()) {
            throw new ParseException(value + " is not a decimal", pos.getIndex());
        }

        return bigDecimal;
    }

    public static String maxCurrencyLimit(String currencyCode) {
        int maxFractionDigits = Currency.getInstance(currencyCode).getDefaultFractionDigits();
        if(maxFractionDigits == 0) {
            return MAX_PRECISION_LIMIT;
        }
        BigDecimal bd = new BigDecimal(MAX_PRECISION_LIMIT);
        return bd.setScale(maxFractionDigits, BigDecimal.ROUND_DOWN).toString().replaceAll("0", "9");
    }

    public static boolean isZeroOrNull(BigDecimal number) {
        return number == null || number.compareTo(BigDecimal.ZERO) == 0;
    }

    public static String minCurrencyLimit(String currencyCode) {
        int maxFractionDigits = Currency.getInstance(currencyCode).getDefaultFractionDigits();
        if(maxFractionDigits > 0) {
            return new StringBuffer(new BigDecimal("0").setScale(maxFractionDigits - 1, BigDecimal.ROUND_DOWN).toString()).append("1").toString();
        }
        
        return MIN_CURRENCY_LIMIT;
    }

    public static DecimalFormat getFormat(Locale locale) {
        return getFormat(locale, -1);
    }

    public static DecimalFormat getFormat(Locale locale, int precision) {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance(locale);
        if (precision >= 0) {
            nf.setMinimumFractionDigits(precision);
            nf.setMaximumFractionDigits(precision);
        }
        return nf;
    }

    public static DecimalFormat getCurrencyFormat(Locale locale, String currencyCode) {
        return getCurrencyFormat(locale, currencyCode, -1);
    }

    public static DecimalFormat getCurrencyFormat(Locale locale, String currencyCode, int precision) {
        DecimalFormat nf = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);

        try {
            Currency currency = Currency.getInstance(currencyCode);
            nf.setCurrency(currency);
            nf.setMinimumFractionDigits(currency.getDefaultFractionDigits());
            nf.setMaximumFractionDigits(precision >= currency.getDefaultFractionDigits() ? precision : currency.getDefaultFractionDigits());
        } catch (IllegalArgumentException e) {
            // Format as simple number if currency code is invalid
            nf.setMaximumFractionDigits(precision >= 2 ? precision : 2);
            nf.setMinimumFractionDigits(2);
        }
        return nf;
    }

    public static BigInteger parseBigInteger(String value) {
        try {
            return new BigInteger(value);
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    public static boolean isBigInteger(String value) {
        try {
            parseBigInteger(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    public static BigDecimal toPercents(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.multiply(ONE_HUNDRED);
    }

    public static BigDecimal fromPercents(BigDecimal value) {
        if (value == null) {
            return null;
        }

        return value.divide(ONE_HUNDRED);
    }

    public static BigDecimal toBigDecimal(Number value) {
        if (value == null) {
            return null;
        } if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Double || value instanceof Short) {
            return BigDecimal.valueOf(value.doubleValue());
        } else if (value instanceof Long || value instanceof Integer || value instanceof Byte) {
            return BigDecimal.valueOf(value.longValue());
        } else {
            throw new IllegalArgumentException("Can't convert " + value.getClass() + " to BigDecimal");
        }
    }

    public static BigDecimal addFraction(BigDecimal value, int fractionDigits) {
        return value.add(minDelta(fractionDigits));
    }

    public static BigDecimal subtractFraction(BigDecimal value, int fractionDigits) {
        return value.subtract(minDelta(fractionDigits));
    }

    private static BigDecimal minDelta(int fractionDigits) {
        return BigDecimal.ONE.scaleByPowerOfTen(-1 * fractionDigits);
    }


    public static String formatNumber(Number value) {
        return formatNumber(value, 0);
    }

    public static String formatNumber(Number value, int scale) {
        NumberFormat nf = NumberFormat.getNumberInstance(CurrentUserSettingsHolder.getLocale());

        nf.setMaximumFractionDigits(scale);
        nf.setMinimumFractionDigits(scale);

        return nf.format(value.doubleValue());
    }

    public static Double toDouble(Number value) {
        return value == null ? null : value.doubleValue();
    }

    public static long safeLong(Long value) {
        return value != null ? value : 0L ;
    }
}
