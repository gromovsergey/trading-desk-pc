package com.foros.reporting.serializer.formatter;

import org.apache.commons.lang.StringEscapeUtils;

public class UrlValueFormatter extends ValueFormatterSupport<String> {

    @Override
    public String formatText(String value, FormatterContext context) {
        return value;
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
        String preparedTextHtml = prepareTextHtml(value, context);
        String preparedUrl = prepareUrl(value);

        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"");
        sb.append(StringEscapeUtils.escapeHtml(preparedUrl));
        sb.append("\" target=\"_blank\">");
        sb.append(preparedTextHtml);
        sb.append("</a>");

        cellAccessor.setHtml(sb.toString());
        cellAccessor.addStyle("url");
    }

    protected String prepareUrl(String value) {
        String lower = value.toLowerCase();
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            return "http://" + value;
        } else {
            return value;
        }
    }

    protected String prepareTextHtml(String value, FormatterContext context) {
        return StringEscapeUtils.escapeHtml(value);
    }
}
