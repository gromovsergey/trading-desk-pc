package com.foros.reporting.serializer.formatter;

public class HighlightValueFormatterWrapper implements ValueFormatter<Object> {

    private ValueFormatter valueFormatter;
    private ValueFormatter classesFormatter;
    private String styleClass;

    public HighlightValueFormatterWrapper(ValueFormatter formatter, String styleClass) {
        this(formatter, formatter, styleClass);
    }

    public HighlightValueFormatterWrapper(ValueFormatter formatter, ValueFormatter classesFormatter, String styleClass) {
        this.valueFormatter = formatter;
        this.classesFormatter = classesFormatter;
        this.styleClass = styleClass;
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
        cellAccessor.addStyle(styleClass);
        valueFormatter.formatExcel(cellAccessor, value, context);
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Object value, FormatterContext context) {
        cellAccessor.addStyle(styleClass);
        valueFormatter.formatHtml(cellAccessor, value, context);
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        return valueFormatter.formatText(value, context);
    }

    @Override
    public String formatCsv(Object value, FormatterContext context) {
        return valueFormatter.formatCsv(value, context);
    }
}
