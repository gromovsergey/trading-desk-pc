package com.foros.reporting.serializer.formatter;

import static com.foros.util.NumberUtil.formatNumber;

import java.math.BigDecimal;

public abstract class NumberWithPercentValueFormatter extends ValueFormatterSupport<BigDecimal> {
    @Override
    public String formatText(BigDecimal value, FormatterContext context) {
        if (value == null) {
            return "";
        }

        BigDecimal percent = BigDecimal.ZERO;
        percent = percent.add(getPercent(context, value));
        return new StringBuilder("")
                .append(formatNumber(value))
                .append(" (")
                .append(formatNumber((percent.equals(BigDecimal.ZERO) ? 0 : percent)))
                .append("%)")
                .toString();
    }

    protected abstract BigDecimal getPercent(FormatterContext context, BigDecimal value);

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
        cellAccessor.setString(formatText(value, context));
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
        cellAccessor.addStyle("number");
        super.formatHtml(cellAccessor, value, context);
    }
}
