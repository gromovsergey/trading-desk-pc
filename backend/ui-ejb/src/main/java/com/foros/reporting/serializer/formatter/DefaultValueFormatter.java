package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;

import java.math.BigDecimal;
import java.util.Date;

public class DefaultValueFormatter extends ValueFormatterSupport<Object> {
    public static final  ValueFormatter<?> INSTANCE = new DefaultValueFormatter();

    @Override
    public String formatText(Object value, FormatterContext context) {
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings({"unchecked"})
    public static <C> ValueFormatter<C> instance() {
        return (ValueFormatter<C>)INSTANCE;
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
        if (value instanceof BigDecimal) {
            cellAccessor.setDouble(((BigDecimal) value).doubleValue());
        } else if (value instanceof Date) {
            cellAccessor.setDate((Date) value);
            cellAccessor.addStyle(Styles.dateTime());
        } else {
            super.formatExcel(cellAccessor, value, context);
        }
    }
}
