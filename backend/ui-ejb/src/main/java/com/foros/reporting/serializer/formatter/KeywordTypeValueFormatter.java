package com.foros.reporting.serializer.formatter;

import com.foros.model.channel.KeywordTriggerType;
import com.foros.util.StringUtil;

import java.util.Locale;

public class KeywordTypeValueFormatter extends ValueFormatterSupport<String> {
    @Override
    public String formatText(String value, FormatterContext context) {
        if (value == null || value.length() < 1) {
            return "";
        }
        Locale locale = context.getLocale();
        KeywordTriggerType keywordType = KeywordTriggerType.byLetter(value.charAt(0));
        return StringUtil.getLocalizedString("enums.KeywordTriggerType." + keywordType.getName(), locale);
    }
}
