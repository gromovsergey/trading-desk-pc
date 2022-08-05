package com.foros.reporting.serializer;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.session.reporting.PreparedParameterBuilder;

public interface ResultSerializer<T extends ResultSerializer> extends ResultHandler {

    T registry(ValueFormatterRegistry registry, RowType rowType);

    T preparedParameters(PreparedParameterBuilder.Factory factory);

    int getMaxRows();

    ResultSerializer summary(MetaData metaData, Row row);

    FormatterContext getFormatterContext();
}
