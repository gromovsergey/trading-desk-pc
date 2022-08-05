package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.DbColumn;

import org.apache.commons.lang.StringEscapeUtils;

public class ConditionEntityUrlValueFormatter extends EntityUrlValueFormatter {

    public static interface Condition {
        boolean isShowUrl(FormatterContext context);
    }

    private Condition condition;

    public ConditionEntityUrlValueFormatter(DbColumn idColumn, String urlPattern, Condition condition) {
        super(idColumn, urlPattern);
        this.condition = condition;
    }

    @Override
    public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
        if (value == null) {
            cellAccessor.setHtml("");
        } else if (condition.isShowUrl(context)) {
            super.formatHtml(cellAccessor, value, context);
        } else {
            String text = formatText(value, context);
            cellAccessor.setHtml(StringEscapeUtils.escapeHtml(text));
        }
    }

}
