package com.foros.reporting.serializer.formatter;

public class NullValueFormatter<T> implements ValueFormatter<T> {

    public static final NullValueFormatter INSTANCE = new NullValueFormatter();

    public static <T> NullValueFormatter<T> getInstance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        return null;
    }

    @Override
    public String formatCsv(Object value, FormatterContext context) {
        return null;
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Object value, FormatterContext context) {
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
    }
}
