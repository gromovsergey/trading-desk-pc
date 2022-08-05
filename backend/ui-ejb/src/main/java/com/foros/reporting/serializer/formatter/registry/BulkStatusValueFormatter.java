package com.foros.reporting.serializer.formatter.registry;

import com.foros.model.Status;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

public class BulkStatusValueFormatter extends ValueFormatterSupport<Status> {
    @Override
    public String formatText(Status value, FormatterContext context) {
        return value == null ? "" : value.name();
    }
}
