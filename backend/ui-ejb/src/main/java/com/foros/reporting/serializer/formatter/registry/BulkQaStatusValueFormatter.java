package com.foros.reporting.serializer.formatter.registry;

import com.foros.model.ApproveStatus;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;

public class BulkQaStatusValueFormatter extends ValueFormatterSupport<ApproveStatus> {
    @Override
    public String formatText(ApproveStatus value, FormatterContext context) {
        return value == null ? "" : value.name();
    }
}
