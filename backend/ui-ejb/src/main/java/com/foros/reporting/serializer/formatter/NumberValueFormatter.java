package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.NumberUtil;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberValueFormatter extends ValueFormatterSupport<Number> {

    private int fractionDigits = -1;

    public NumberValueFormatter() {
    }

    public NumberValueFormatter(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    private String formatNumber(Number value, Locale locale, boolean groupingUsed) {
        value = prepare(value);
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        if (fractionDigits >= 0) {
            nf.setMinimumFractionDigits(fractionDigits);
            nf.setMaximumFractionDigits(fractionDigits);
        }
        nf.setGroupingUsed(groupingUsed);
        return nf.format(value);
    }

    @Override
    public String formatText(Number value, FormatterContext context) {
        return formatNumber(value, context.getLocale(), true);
    }

    @Override
    public String formatCsv(Number value, FormatterContext context) {
        return formatNumber(value, Locale.US, false);
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Number value, FormatterContext context) {
        cellAccessor.addStyle("number");
        super.formatHtml(cellAccessor, value, context);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Number value, FormatterContext context) {
        value = prepare(value);
        cellAccessor.setDouble(NumberUtil.toDouble(value));
        if (fractionDigits >= 0) {
            cellAccessor.addStyle(Styles.number(fractionDigits));
        }
    }

    private Number prepare(Number value) {
        if (value == null) {
            value = 0;
        }
        return value;
    }
}
