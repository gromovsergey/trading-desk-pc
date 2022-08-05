package com.foros.reporting.serializer.formatter;

import static com.foros.util.NumberUtil.formatNumber;
import com.foros.reporting.meta.DbColumn;

import java.math.BigDecimal;

public class ImpressionValueFormatter extends ValueFormatterSupport<BigDecimal> {

    private DbColumn pcColumn;

    public ImpressionValueFormatter(DbColumn pcColumn) {
        this.pcColumn = pcColumn;
    }

    @Override
    public String formatText(BigDecimal value, FormatterContext context) {
        if (value == null) {
            return "";
        }

        BigDecimal percent = BigDecimal.ZERO;
        percent = percent.add(getPercent(context));
        return value + " ( " + formatNumber((percent.equals(BigDecimal.ZERO) ? 0 : percent), 2) + " %)";
    }

    private BigDecimal getPercent(FormatterContext context) {
        return (BigDecimal) context.getRow().get(pcColumn);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
        cellAccessor.setString(formatText(value, context));
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, BigDecimal value, FormatterContext context) {
        super.formatHtml(cellAccessor, value, context);
    }
}
