package com.foros.reporting.serializer.formatter;

import com.foros.model.time.TimeSpan;

import java.text.NumberFormat;

public class TimeSpanValueFormatter extends ValueFormatterSupport<TimeSpan> {
    @Override
    public String formatText(TimeSpan value, FormatterContext context) {
        if (value == null) {
            return null;
        }

        NumberFormat nf = NumberFormat.getNumberInstance(context.getLocale());
        return nf.format(value.getValue()) + value.getUnit().getUnitValue();
    }
}
