package com.foros.reporting.serializer.formatter;

public class NullValueFormatterWrapper extends ValueFormatterSupport<Object> {
    private ValueFormatter<Object> formatter;

    public NullValueFormatterWrapper(ValueFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        return formatter.formatText(value, context);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
        if (value == null) {
            return;
        }

        formatter.formatExcel(cellAccessor, value, context);
    }
}
