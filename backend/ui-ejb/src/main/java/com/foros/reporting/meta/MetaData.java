package com.foros.reporting.meta;

import java.util.Collection;
import java.util.List;

public interface MetaData<C extends Column> {

    ColumnsMetaData<C> getColumnsMeta();

    List<C> getColumns();

    MetaData<C> retain(Collection<C> columns);

    MetaData<C> retain(C... columns);

    MetaData<C> exclude(Collection<C> columns);

    MetaData<C> exclude(C... excludedColumns);

    MetaData<C> include(Collection<C> columns);

    MetaData<C> include(C...columns);

    boolean contains(String id);

    boolean contains(C column);

    C find(String id);

}
