package com.foros.reporting.serializer.formatter;

public interface HtmlCellAccessor {
    void addStyle(String style);

    void setHtml(String html);

    String getHtml();
}
