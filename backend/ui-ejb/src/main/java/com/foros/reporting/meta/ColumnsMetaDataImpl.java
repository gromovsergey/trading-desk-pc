package com.foros.reporting.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColumnsMetaDataImpl<C extends Column> implements ColumnsMetaData<C> {

    private List<C> columns;
    private Map<String, C> index;

    public ColumnsMetaDataImpl(List<C> columns) {
        this.columns = Collections.unmodifiableList(columns);
        this.index = createIndex(columns);
    }

    private Map<String, C> createIndex(List<C> columns) {
        LinkedHashMap<String, C> index = new LinkedHashMap<>();

        for (C column : columns) {
            index.put(column.getNameKey(), column);
        }

        return index;
    }

    @Override
    public List<C> getColumns() {
        return columns;
    }

    @Override
    public boolean contains(C column) {
        return index.containsValue(column);
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
    public ColumnsMetaData<C> retainById(Collection<String> ids) {
        ArrayList<C> result = new ArrayList<>();

        for (String id : ids) {
            C column = findById(id);
            if (column != null) {
                result.add(column);
            }
        }

        return new ColumnsMetaDataImpl<>(result);
    }

    @Override
    public ColumnsMetaData<C> retain(Collection<C> columns) {
        ArrayList<C> result = new ArrayList<>(this.columns);

        result.retainAll(columns);

        return new ColumnsMetaDataImpl<>(result);
    }

    @Override
    public ColumnsMetaData<C> exclude(Collection<C> columns) {
        ArrayList<C> result = new ArrayList<>(this.columns);

        result.removeAll(columns);

        return new ColumnsMetaDataImpl<>(result);
    }

    @Override
    public ColumnsMetaData<C> include(Collection<C> columns) {
        ArrayList<C> result = new ArrayList<>(this.columns);

        result.addAll(columns);

        return new ColumnsMetaDataImpl<>(result);
    }

    @Override
    public C findById(String id) {
        return index.get(id);
    }

}
