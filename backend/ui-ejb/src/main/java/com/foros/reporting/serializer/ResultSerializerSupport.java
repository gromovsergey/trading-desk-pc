package com.foros.reporting.serializer;

import com.foros.reporting.ReportingException;
import com.foros.reporting.Row;
import com.foros.reporting.RowType;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryChain;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistryHolder;
import com.foros.session.reporting.PreparedParameterBuilder;

public abstract class ResultSerializerSupport<T extends ResultSerializer> implements ResultSerializer<T> {
    protected final ValueFormatterRegistryHolder registryHolder;
    protected final FormatterContext context;
    protected ValueFormatterRegistryChain registry;
    protected MetaData metaData;

    protected ResultSerializerSupport() {
        this(null, null);
    }

    public ResultSerializerSupport(ValueFormatterRegistryHolder registryHolder, FormatterContext context) {
        this.registryHolder = ValueFormatterRegistries.defaultAnd(registryHolder);
        this.context = context;
    }

    @Override
    public T registry(ValueFormatterRegistry registry, RowType rowType) {
        this.registryHolder.registries(registry, rowType);
        return self();
    }

    @Override
    public T preparedParameters(PreparedParameterBuilder.Factory factory) {
        return self();
    }

    @Override
    public int getMaxRows() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public ResultSerializer summary(MetaData metaData, Row row) {
        registry = registryHolder.registry(row.getType());
        return this;
    }

    @Override
    public FormatterContext getFormatterContext() {
        return context;
    }

    @SuppressWarnings({"unchecked"})
    private T self() {
        return (T) this;
    }

    @Override
    public void before(MetaData metaData) {
        this.metaData = metaData;
        registry = registryHolder.registry(RowTypes.header());
    }

    @Override
    public void row(Row row) {
        context.setRow(row);
        registry = registryHolder.registry(row.getType());
    }

    @Override
    public void after() {
    }

    @Override
    public void close() {
    }

    @Override
    public void onError(ReportingException ex) {
    }
}
