package com.foros.action.bulk;

import com.foros.reporting.meta.Column;
import com.foros.reporting.meta.ColumnsMetaData;
import com.foros.reporting.meta.MetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BulkMetaData<T extends Column> implements MetaData<T> {

    private final List<T> columns;
    private Map<Column, Integer> index;

    public BulkMetaData(List<T> columns) {
        this.columns = new ArrayList<T>(columns);
        this.index = new HashMap<Column, Integer>(columns.size());

        for (int i = 0; i < columns.size(); i++) {
             index.put(columns.get(i), i);
        }
    }

    public BulkMetaData(T... columns) {
        this(Arrays.asList(columns));
    }

    @Override
    public List<T> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public BulkMetaData<T> exclude(T... excludedFields) {
        return exclude(new HashSet<T>(Arrays.asList(excludedFields)));
    }

    public BulkMetaData<T> exclude(Set<T> excludedFields) {
        if (excludedFields == null || excludedFields.size() == 0) {
            return this;
        }

        List<T> newColumns = new ArrayList<T>(columns.size());
        for (T column : columns) {
            if (!excludedFields.contains(column)) {
                newColumns.add(column);
            }
        }

        return new BulkMetaData<T>(newColumns);
    }

    public BulkMetaData<T> append(T... columns) {
        List<T> newColumns = new ArrayList<T>(this.columns);
        newColumns.addAll(Arrays.asList(columns));
        return new BulkMetaData<T>(newColumns);
    }

    public int size() {
        return columns.size();
    }

    public int indexOf(Column column) {
        Integer i = index.get(column);
        return i == null ? -1 : i;
    }

    @Override
    public ColumnsMetaData<T> getColumnsMeta() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MetaData<T> retain(Collection<T> columns) {
        return null;
    }

    @Override
    public MetaData<T> retain(T... columns) {
        return null;
    }

    @Override
    public MetaData<T> exclude(Collection<T> columns) {
        return null;
    }

    @Override
    public MetaData<T> include(Collection<T> columns) {
        return null;
    }

    @Override
    public MetaData<T> include(T... columns) {
        return null;
    }

    @Override
    public boolean contains(String id) {
        return false;
    }

    @Override
    public boolean contains(T column) {
        return index.get(column) != null;
    }

    @Override
    public T find(String id) {
        return null;
    }
}
