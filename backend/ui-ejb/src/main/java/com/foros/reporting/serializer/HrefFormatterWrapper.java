package com.foros.reporting.serializer;

import com.foros.reporting.serializer.formatter.ExcelCellAccessor;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.ValueFormatter;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

import org.apache.commons.lang.StringEscapeUtils;

public abstract class HrefFormatterWrapper extends ValueFormatterSupport<Object> {

    private ValueFormatter formatter;

    public HrefFormatterWrapper(ValueFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String formatText(Object value, FormatterContext context) {
        return formatter.formatText(value, context);
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Object value, FormatterContext context) {
        String text = StringEscapeUtils.escapeHtml(formatter.formatText(value, context));
        String href = StringEscapeUtils.escapeHtml(getHref(text, context));

        StringBuilder sb = new StringBuilder()
                .append("<a href=\"")
                .append(href)
                .append("\">")
                .append(text)
                .append("</a>");

        cellAccessor.setHtml(sb.toString());
        cellAccessor.addStyle(getStyle());
    }

    @Override
    public void formatExcel(ExcelCellAccessor cellAccessor, Object value, FormatterContext context) {
        formatter.formatExcel(cellAccessor, value, context);
    }

    protected String getStyle(){
        return "url";
    }

    protected abstract String getHref(String text, FormatterContext context);
}
