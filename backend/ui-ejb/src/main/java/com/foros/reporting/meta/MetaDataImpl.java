package com.foros.reporting.meta;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MetaDataImpl<C extends Column> implements MetaData<C> {

    private ColumnsMetaData<C> columnsMeta;

    public MetaDataImpl(C... columns) {
        this(Arrays.asList(columns));
    }

    public MetaDataImpl(List<C> columns) {
        this.columnsMeta = new ColumnsMetaDataImpl<>(columns);
    }

    private MetaDataImpl(ColumnsMetaData<C> columnsMeta) {
        this.columnsMeta = columnsMeta;
    }

    @Override
    public ColumnsMetaData<C> getColumnsMeta() {
        return columnsMeta;
    }

    @Override
    public List<C> getColumns() {
        return columnsMeta.getColumns();
    }

    @Override
    public MetaData<C> retain(Collection<C> columns) {
        return new MetaDataImpl<>(columnsMeta.retain(columns));
    }

    @Override
    public MetaData<C> retain(C... columns) {
        return retain(Arrays.asList(columns));
    }

    @Override
    public MetaData<C> exclude(Collection<C> columns) {
        return new MetaDataImpl<>(columnsMeta.exclude(columns));
    }

    @Override
    public MetaData<C> exclude(C... excludedColumns) {
        return exclude(Arrays.asList(excludedColumns));
    }

    @Override
    public MetaData<C> include(Collection<C> columns) {
        return new MetaDataImpl<>(columnsMeta.include(columns));
    }

    @Override
    public MetaData<C> include(C... columns) {
        return include(Arrays.asList(columns));
    }

    @Override
    public boolean contains(String id) {
        return columnsMeta.contains(id);
    }

    @Override
    public boolean contains(C column) {
        return columnsMeta.contains(column);
    }

    @Override
    public C find(String id) {
        return columnsMeta.findById(id);
    }
}
