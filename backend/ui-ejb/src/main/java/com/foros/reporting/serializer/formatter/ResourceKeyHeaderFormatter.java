package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.util.StringUtil;

public class ResourceKeyHeaderFormatter extends HeaderFormatterSupport<Column> {
    private String key;

    public ResourceKeyHeaderFormatter(String key) {
        this.key = key;
    }

    @Override
    public String formatText(Column column, FormatterContext context) {
        return StringUtil.getLocalizedStringWithDefault(key, key, context.getLocale());
    }
}
