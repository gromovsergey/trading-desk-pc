package com.foros.reporting.tools;

import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.ResultSerializer;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.session.reporting.PreparedParameterBuilder;

public class ResultSerializerWrapper extends ResultHandlerWrapper implements ResultSerializer {

    private ResultSerializer target;

    public ResultSerializerWrapper(ResultSerializer resultSerializer) {
        super(resultSerializer);
        this.target = resultSerializer;
    }

    @Override
    public ResultSerializer registry(ValueFormatterRegistry registry, RowType rowType) {
        target.registry(registry, rowType);
        return this;
    }

    @Override
    public ResultSerializer preparedParameters(PreparedParameterBuilder.Factory factory) {
        target.preparedParameters(factory);
        return this;
    }

    @Override
    public int getMaxRows() {
        return target.getMaxRows();
    }

    @Override
    public ResultSerializer summary(MetaData metaData, Row row) {
        target.summary(metaData, row);
        return this;
    }

    @Override
    public FormatterContext getFormatterContext() {
        return target.getFormatterContext();
    }

    public ResultSerializer getTarget() {
        return target;
    }
}
