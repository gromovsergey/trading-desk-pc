package com.foros.reporting.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ReportColumnsMetaDataImpl<C extends DependentColumn<C>> implements ReportColumnsMetaData<C> {

    private List<C> columns;
    private List<C> originalColumns;
    private Set<C> columnsWithDependencies;
    private Map<String, C> index; // todo: better index for find operation
    private Object context;

    ReportColumnsMetaDataImpl(List<C> columns, Object context) {
        this(columns, columns, context);
    }

    private ReportColumnsMetaDataImpl(
            List<C> columns,
            List<C> originalColumns,
            Object context) {
        this.columns = Collections.unmodifiableList(columns);
        this.originalColumns = Collections.unmodifiableList(new ArrayList<C>(originalColumns));
        this.columnsWithDependencies = Collections.unmodifiableSet(getColumnsWithDependencies(columns, context));
        this.index = Collections.unmodifiableMap(createIndex(columnsWithDependencies));
        this.context = context;
    }

    protected Map<String, C> createIndex(Collection<C> cols) {
        Map<String, C> index = new HashMap<>();

        for (C column : cols) {
            index.put(column.getNameKey(), column);
        }

        return index;
    }

    private Set<C> getColumnsWithDependencies(List<C> cols, Object context) {
        Set<C> result = new LinkedHashSet<>();

        for (C column : cols) {
            result.add(column);
            result.addAll(column.getDependentColumns(context));
        }

        return result;
    }

    @Override
    public List<C> getColumns() {
        return columns;
    }

    @Override
    public Set<C> getColumnsWithDependencies() {
        return columnsWithDependencies;
    }

    @Override
    public boolean contains(C column) {
        return columnsWithDependencies.contains(column);
    }

    @Override
    public boolean contains(String id) {
        return index.containsKey(id);
    }

    @Override
    public boolean containsAny(Collection<C> columns) {
        for (C column : columns) {
            if (contains(column)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ReportColumnsMetaData<C> retain(Collection<C> columns) {
        List<C> result = new ArrayList<>(getColumns());

        result.retainAll(columns);

        return new ReportColumnsMetaDataImpl<>(result, originalColumns, context);
    }

    @Override
    public ReportColumnsMetaData<C> exclude(Collection<C> columns) {
        ArrayList<C> result = new ArrayList<>(getColumns());

        result.removeAll(columns);

        return new ReportColumnsMetaDataImpl<>(result, originalColumns, context);
    }

    @Override
    public ReportColumnsMetaData<C> include(Collection<C> columns) {
        ArrayList<C> result = new ArrayList<>();

        for (C column : originalColumns) {
            if (this.columns.contains(column) || columns.contains(column)) {
                result.add(column);
            }
        }

        return new ReportColumnsMetaDataImpl<>(result, originalColumns, context);
    }

    @Override
    public void replace(C from, C to) {
        ArrayList<C> result = new ArrayList<>(getColumns());

        int index = result.indexOf(from);
        if (index >= 0) {
            result.set(index, to);
        }

        this.columns = Collections.unmodifiableList(result);
        this.columnsWithDependencies = Collections.unmodifiableSet(getColumnsWithDependencies(columns, context));
        this.index = Collections.unmodifiableMap(createIndex(columnsWithDependencies));
    }

    @Override
    public C findById(String id) {
        return index.get(id);
    }

    @Override
    public ReportColumnsMetaData<C> retainById(Collection<String> ids) {
        List<C> result = new ArrayList<>();

        for (C column : getColumns()) {
            if (ids.contains(column.getNameKey())) {
                result.add(column);
            }
        }

        return new ReportColumnsMetaDataImpl<>(result, originalColumns, context);
    }

    @Override
    public String toString() {
        return "ColumnsMetaDataImpl{" +
                "columnsWithDependencies=" + columnsWithDependencies +
                '}';
    }
}
