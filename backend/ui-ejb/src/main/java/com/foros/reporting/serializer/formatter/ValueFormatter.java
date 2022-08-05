package com.foros.reporting.serializer.formatter;

public interface ValueFormatter<T> {

    String formatText(T value, FormatterContext context);

    /**
     * IMPORTANT: numbers should be formatted without commas (e.g. using EN_US locale) to be able to import CSV to Excel
     */
    String formatCsv(T value, FormatterContext context);

    void formatHtml(HtmlCellAccessor cellAccessor, T value, FormatterContext context);

    void formatExcel(ExcelCellAccessor cellAccessor, T value, FormatterContext context);
}
