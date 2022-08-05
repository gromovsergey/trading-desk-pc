package com.foros.reporting.serializer.formatter;

import com.foros.model.LocalizableName;
import com.foros.reporting.meta.Column;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.i18n.LocalizableNameProvider;

public class EntityUrlLocalisableValueFormatter extends EntityUrlValueFormatter {
    private LocalizableNameProvider provider;

    public EntityUrlLocalisableValueFormatter(LocalizableNameProvider provider, Column idColumn, String urlPattern) {
        this(provider, idColumn, urlPattern, null);
    }

    public EntityUrlLocalisableValueFormatter(LocalizableNameProvider provider, Column idColumn, String urlPattern, String baseUrl) {
        super(idColumn, urlPattern, baseUrl);
        this.provider = provider;
    }

    @Override
    public String formatText(String value, FormatterContext context) {
        LocalizableName ln = provider.provide(value, context.getRow().get(idColumn));
        return LocalizableNameUtil.getLocalizedValue(ln);
    }
}
