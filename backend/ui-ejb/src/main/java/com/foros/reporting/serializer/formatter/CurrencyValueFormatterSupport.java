package com.foros.reporting.serializer.formatter;

import com.foros.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class CurrencyValueFormatterSupport extends ValueFormatterSupport<Number> {

    private final int precision;

    protected CurrencyValueFormatterSupport() {
        this(-1);
    }

    protected CurrencyValueFormatterSupport(int precision) {
        this.precision = precision;
    }

    @Override
    public String formatText(Number value, FormatterContext context) {
        String currencyCode = getCurrencyCode(context);

        if (value == null && currencyCode == null) {
            return "";
        }

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        DecimalFormat nf;
        if (currencyCode != null) {
            nf = NumberUtil.getCurrencyFormat(context.getLocale(), currencyCode, precision);
        } else {
            // Sometime data is wrong and no currency for the decimal.
            // Show it as is in this case. It will allow to investigate issues more easily.
            nf = NumberUtil.getFormat(context.getLocale(), precision);
        }

        BigDecimal bdValue = NumberUtil.toBigDecimal(value);
        bdValue = convertValue(bdValue);
        return nf.format(bdValue.setScale(nf.getMaximumFractionDigits(), RoundingMode.HALF_UP));
    }

    protected BigDecimal convertValue(BigDecimal value) {
        return value;
    }

    protected abstract String getCurrencyCode(FormatterContext context);

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Number value, FormatterContext context) {
        String currencyCode = getCurrencyCode(context);

        if (value == null && currencyCode == null) {
            return;
        }

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        BigDecimal bdValue = NumberUtil.toBigDecimal(value);
        bdValue = convertValue(bdValue);

        cellAccessor.setDouble(NumberUtil.toDouble(bdValue));
        if (currencyCode != null) {
            cellAccessor.addStyle("currency." + currencyCode);
        }
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Number value, FormatterContext context) {
        cellAccessor.addStyle("number");
        super.formatHtml(cellAccessor, value, context);
    }
}
