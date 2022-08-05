package com.foros.reporting.meta;

import java.util.ArrayList;
import java.util.List;

public class ResolvableMetaDataImpl<C extends DependentColumn<C>> implements ResolvableMetaData<C> {

    protected final String nameKey;
    protected final List<C> metricColumns;
    protected final List<C> outputColumns;
    protected final List<C> allColumns;

    public ResolvableMetaDataImpl(
            final String nameKey,
            final List<C> metricColumns,
            final List<C> outputColumns) {
        this.nameKey = nameKey;
        this.metricColumns = metricColumns;
        this.outputColumns = outputColumns;
        this.allColumns = new ArrayList<>();
        this.allColumns.addAll(outputColumns);
        this.allColumns.addAll(metricColumns);
    }

    @Override
    public List<C> getColumns() {
        return allColumns;
    }

    @Override
    public List<C> getMetricsColumns() {
        return metricColumns;
    }

    @Override
    public List<C> getOutputColumns() {
        return outputColumns;
    }

    @Override
    public ReportMetaData<C> resolve(Object context) {
        return new ReportMetaDataImpl<>(nameKey, metricColumns, outputColumns, context);
    }
}
