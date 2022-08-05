package com.foros.reporting.serializer.formatter;

import com.foros.model.LocalizableName;
import com.foros.reporting.meta.DbColumn;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.i18n.LocalizableNameProvider;

public class LocalizableNameValueFormatter extends ValueFormatterSupport<String> {

    private DbColumn IdColumn;

    private LocalizableNameProvider provider;

    public LocalizableNameValueFormatter(LocalizableNameProvider provider, DbColumn idColumn) {
        this.provider = provider;
        IdColumn = idColumn;
    }

    @Override
    public String formatText(String value, FormatterContext context) {
        LocalizableName ln = provider.provide(value, context.getRow().get(IdColumn));
        return LocalizableNameUtil.getLocalizedValue(ln);
    }
}
