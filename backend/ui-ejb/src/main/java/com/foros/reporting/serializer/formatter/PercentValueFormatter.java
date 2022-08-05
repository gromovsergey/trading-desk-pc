package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;
import com.foros.util.NumberUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class PercentValueFormatter extends ValueFormatterSupport<Number> {

    int fraction;
    public PercentValueFormatter() {
        this(2);
    }

    public PercentValueFormatter(int fraction) {
        this.fraction = fraction;
    }

    private String formatNumber(Number value, Locale locale, boolean groupingUsed) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }

        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMaximumFractionDigits(fraction);
        nf.setMinimumFractionDigits(fraction);
        nf.setGroupingUsed(groupingUsed);

        BigDecimal bd = NumberUtil.toBigDecimal(value);

        return nf.format(bd) + "%";
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
        if (value == null) {
            value = 0.;
        }

        cellAccessor.setDouble(NumberUtil.toDouble(value) / 100);
        cellAccessor.addStyle(Styles.percent());
    }
}
