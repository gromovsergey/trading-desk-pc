package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;

public class HeaderWithHintFomratter extends HeaderFormatter {

    private String tipKey;

    public HeaderWithHintFomratter(String tipKey) {
        this.tipKey = tipKey;
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, Column value, FormatterContext context) {
        StringBuilder sb = new StringBuilder()
                .append("<span class=\"textWithHint\">")
                .append(StringEscapeUtils.escapeHtml(formatText(value, context)))
                .append("<div class=\"hintContainer\">")
                .append("<div class=\"hintSign\">")
                .append("<div class=\"toolTip\">")
                .append(StringEscapeUtils.escapeHtml(StringUtil.getLocalizedStringWithDefault(tipKey, tipKey, context.getLocale())))
                .append("</div>")
                .append("</div>")
                .append("</span>");
        cellAccessor.setHtml(sb.toString());
    }
}
