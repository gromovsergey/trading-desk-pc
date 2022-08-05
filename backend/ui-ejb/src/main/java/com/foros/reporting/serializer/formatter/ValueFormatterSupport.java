package com.foros.reporting.serializer.formatter;

import com.foros.reporting.serializer.xlsx.Styles;

import org.apache.commons.lang.StringEscapeUtils;

public abstract class ValueFormatterSupport<T> implements ValueFormatter<T> {
    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, T value, FormatterContext context) {
        formatHtmlDefault(cellAccessor, value, context);
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, T value, FormatterContext context) {
        formatExcelDefault(cellAccessor, value, context);
    }

    protected void setStyleId(ExcelCellAccessor cellAccessor, T value, FormatterContext context) {
        cellAccessor.addStyle(Styles.text());
    }

    protected void setValue(ExcelCellAccessor cellAccessor, T value, FormatterContext context) {
        cellAccessor.setString(formatText(value, context));
    }

    protected void formatHtmlDefault(HtmlCellAccessor cellAccessor, T value, FormatterContext context) {
        String text = formatText(value, context);
        cellAccessor.setHtml(StringEscapeUtils.escapeHtml(text));
    }

    protected void formatExcelDefault(ExcelCellAccessor cellAccessor, T value, FormatterContext context) {
        setValue(cellAccessor, value, context);
        setStyleId(cellAccessor, value, context);
    }

    @Override
    public String formatCsv(T value, FormatterContext context) {
        return formatText(value, context);
    }
}
