package com.foros.reporting.serializer.formatter;

import java.util.Arrays;
import org.apache.commons.lang.StringEscapeUtils;

public class EllipsisedValueFormatter extends ValueFormatterSupport<String> {

    private int maxLength;
    private int breakAfter;
    private int showLast;
    private String ellipsis;

    public EllipsisedValueFormatter(int maxLength, int breakAfter, int showLast, int dotsCount) {
        this.maxLength = maxLength;
        this.breakAfter = breakAfter;
        this.showLast = showLast;
        this.ellipsis = dots(dotsCount);
    }

    private static String dots(int n) {
        char[] chars = new char[n];
        Arrays.fill(chars, '.');
        return new String(chars);
    }

    @Override
    public String formatText(String value, FormatterContext context) {
        if (value != null && value.length() > maxLength) {
            value = value.substring(0, breakAfter) + ellipsis + value.substring(value.length() - showLast);
        }
        return value;
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
        String text = formatText(value, context);
        if (text.equals(value)) {
            super.formatHtml(cellAccessor, value, context);
        } else {
            cellAccessor.setHtml(
                    "<span title=\"" + StringEscapeUtils.escapeHtml(value) + "\">"
                    + StringEscapeUtils.escapeHtml(text)
                    + "</span>"
            );
        }
    }
}
