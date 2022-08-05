package com.foros.reporting.meta;

import java.util.Collection;
import java.util.List;

public interface ColumnsMetaData<C extends Column> {

    List<C> getColumns();

    boolean contains(C column);

    boolean contains(String id);

    boolean containsAny(Collection<C> columns);

    ColumnsMetaData<C> retainById(Collection<String> ids);

    ColumnsMetaData<C> retain(Collection<C> columns);

    ColumnsMetaData<C> exclude(Collection<C> columns);

    ColumnsMetaData<C> include(Collection<C> columns);

    C findById(String id);
}
