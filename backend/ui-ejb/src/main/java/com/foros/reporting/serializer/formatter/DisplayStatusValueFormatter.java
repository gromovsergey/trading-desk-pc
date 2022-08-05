package com.foros.reporting.serializer.formatter;

import com.foros.model.DisplayStatus;
import com.foros.util.StringUtil;

import java.util.Locale;
import java.util.Map;

public class DisplayStatusValueFormatter extends ValueFormatterSupport<Long> {

    private Map<Long,DisplayStatus> displayStatusMap;

    public DisplayStatusValueFormatter(Map<Long, DisplayStatus> displayStatusMap) {
        this.displayStatusMap = displayStatusMap;
    }

    @Override
    public String formatText(Long value, FormatterContext context) {

        if (value == null) {
            return "";
        }
        DisplayStatus displayStatus = displayStatusMap.get(value);

        if (displayStatus == null) {
            return "";
        }
        Locale locale = context.getLocale();
        return StringUtil.getLocalizedString(displayStatus.getDescription(),locale);
    }
}
