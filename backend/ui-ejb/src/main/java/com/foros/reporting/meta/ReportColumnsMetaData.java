package com.foros.reporting.meta;

import java.util.Collection;
import java.util.Set;

public interface ReportColumnsMetaData<C extends DependentColumn<C>> extends ColumnsMetaData<C> {

    Set<C> getColumnsWithDependencies();

    ReportColumnsMetaData<C> retainById(Collection<String> ids);

    ReportColumnsMetaData<C> retain(Collection<C> columns);

    ReportColumnsMetaData<C> exclude(Collection<C> columns);

    ReportColumnsMetaData<C> include(Collection<C> columns);

    void replace(C from, C to);

}
