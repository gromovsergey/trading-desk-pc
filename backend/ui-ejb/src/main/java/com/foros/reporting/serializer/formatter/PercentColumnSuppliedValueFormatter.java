package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.DbColumn;

import java.math.BigDecimal;

public class PercentColumnSuppliedValueFormatter extends NumberWithPercentValueFormatter {

    private DbColumn percentColumn;

    public PercentColumnSuppliedValueFormatter(DbColumn percentColumn) {
        this.percentColumn = percentColumn;
    }

    @Override
    protected BigDecimal getPercent(FormatterContext context, BigDecimal value) {
        return (BigDecimal) context.getRow().get(percentColumn);
    }

}
