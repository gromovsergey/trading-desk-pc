package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;

public class HintHeaderFormatter extends HeaderFormatterSupport<Column> {

    private ValueFormatter formatter;

    public HintHeaderFormatter(ValueFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String formatText(Column column, FormatterContext context) {
        return formatter.formatText(column, context);
    }

    private String getHintableText(Column column, FormatterContext context) {
        String key = column.getNameKey() + ".hint";
        return StringUtil.getLocalizedStringWithDefault(key, key, context.getLocale());
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Column column, FormatterContext context) {
        StringBuilder sb = new StringBuilder()
                .append("<span class=\"textWithHint\">")
                .append(StringEscapeUtils.escapeHtml(formatText(column, context)))
                .append("<div class=\"hintContainer\">")
                .append("<div class=\"hintSign\">")
                .append("<div class=\"toolTip\">")
                .append(getHintableText(column, context))
                .append("</div>")
                .append("</div>")
                .append("</span>");
        cellAccessor.setHtml(sb.toString());
    }
}
