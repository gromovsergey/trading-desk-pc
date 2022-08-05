package app.programmatic.ui.reporting.tools;

import app.programmatic.ui.common.tool.formatting.DateTimeFormatterWrapper;
import app.programmatic.ui.reporting.model.ReportColumnType;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class ReportColumnFormatter {
    private final static String PERCENTAGE_SIGN = "%";
    private final static String RUSSIAN_RUBLE_OLD_SIGN = "руб.";
    private final static String RUSSIAN_RUBLE_SIGN = Character.toString((char)0x20BD);
    private final static int PERCENTAGE_SCALE = 2;

    private final DateTimeFormatter dateFormatter;
    private final String dateFormat;
    private final NumberFormat numberFormat;
    private final NumberFormat percentFormat;
    private final NumberFormat currencyFormat;
    private final String currencySign;
    private final int scale;
    private final Locale locale;

    private final ConcurrentHashMap<String, NumberFormat> currencyFormats = new ConcurrentHashMap<>();

    public ReportColumnFormatter(Locale locale, String currencySign, int scale) {
        this.currencySign = currencySign;
        this.scale = scale;
        this.locale = locale;

        // Issue #243: report is moved to hard-coded date format, please
        // return back when multi-language will be needed
        this.dateFormatter = DateTimeFormatterWrapper.DATE_FORMATTER;
        this.dateFormat = DateTimeFormatterWrapper.DATE_FORMAT;

        this.numberFormat = NumberFormat.getInstance(locale);
        this.numberFormat.setMaximumFractionDigits(scale);

        this.percentFormat = NumberFormat.getInstance(locale);
        this.percentFormat.setMaximumFractionDigits(getPercentageScale());

        this.currencyFormat = NumberFormat.getCurrencyInstance(locale);
        this.currencyFormat.setMaximumFractionDigits(scale);
        this.currencyFormat.setCurrency(Currency.getInstance(currencySign));

        this.currencyFormats.putIfAbsent(currencySign, this.currencyFormat);
    }

    public String format(Object value, ReportColumnType columnType) {
        switch (columnType) {
            case DATE_COLUMN:
                return ((LocalDate) value).format(dateFormatter);
            case TEXT_COLUMN:
                return (String) value;
            case INT_COLUMN:
            case FLOAT_COLUMN:
                return numberFormat.format(value);
            case PERCENT_COLUMN:
                return percentFormat.format(value) + PERCENTAGE_SIGN;
            case CURRENCY_COLUMN:
                // ToDo: remove workaround 'replace(RUSSIAN_RUBLE_OLD_SIGN, RUSSIAN_RUBLE_SIGN)' after #488 (moving to Java 9)
                return currencyFormat.format(value).replace(RUSSIAN_RUBLE_OLD_SIGN, RUSSIAN_RUBLE_SIGN);
            default:
                throw new IllegalArgumentException("Unexpected ReportColumnType: " + columnType);
        }
    }

    public String format(Object value, ReportColumnType columnType, String currencySign) {
        if (currencySign != null && columnType == ReportColumnType.CURRENCY_COLUMN) {
            NumberFormat currencyFormat = findCurrencyFormat(currencySign);
            // ToDo: remove workaround 'replace(RUSSIAN_RUBLE_OLD_SIGN, RUSSIAN_RUBLE_SIGN)' after #488 (moving to Java 9)
            return currencyFormat.format(value).replace(RUSSIAN_RUBLE_OLD_SIGN, RUSSIAN_RUBLE_SIGN);
        }

        return format(value, columnType);
    }

    public String getPercentageSign() {
        return PERCENTAGE_SIGN;
    }

    public int getPercentageScale() {
        return PERCENTAGE_SCALE;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public int getScale() {
        return scale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    private NumberFormat findCurrencyFormat(String currencySign) {
        NumberFormat result = this.currencyFormats.get(currencySign);
        if (result == null) {
            result = NumberFormat.getCurrencyInstance(this.locale);
            result.setMaximumFractionDigits(this.scale);
            result.setCurrency(Currency.getInstance(currencySign));

            this.currencyFormats.putIfAbsent(currencySign, result);
        }

        return result;
    }
}
