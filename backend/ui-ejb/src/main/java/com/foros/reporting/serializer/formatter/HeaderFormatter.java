package com.foros.reporting.serializer.formatter;

import com.foros.reporting.meta.Column;
import com.foros.util.StringUtil;

public class HeaderFormatter extends HeaderFormatterSupport<Column> {

    public static final ValueFormatter<Column> INSTANCE = new HeaderFormatter();

    @Override
    public String formatText(Column column, FormatterContext context) {
        String key = column.getNameKey();
        return StringUtil.getLocalizedStringWithDefault(key, key, context.getLocale());
    }
}
