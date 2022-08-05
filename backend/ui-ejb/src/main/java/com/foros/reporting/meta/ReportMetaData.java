package com.foros.reporting.meta;

import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.session.reporting.parameters.ColumnOrderTO;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface ReportMetaData<C extends DependentColumn<C>> extends MetaData<C> {

    String getName(Locale locale);

    @Override
    ReportColumnsMetaData<C> getColumnsMeta();

    ReportColumnsMetaData<C> getOutputColumnsMeta();

    ReportColumnsMetaData<C> getMetricsColumnsMeta();

    List<ColumnOrder<C>> getSortColumns();

    List<C> getOutputColumns();

    List<C> getMetricsColumns();

    @Override
    ReportMetaData<C> retain(Collection<C> columns);

    @Override
    ReportMetaData<C> retain(C... columns);

    @Override
    ReportMetaData<C> exclude(Collection<C> columns);

    @Override
    ReportMetaData<C> exclude(C... columns);

    @Override
    ReportMetaData<C> include(Collection<C> columns);

    @Override
    ReportMetaData<C> include(C... columns);

    /* Retain by id */

    ReportMetaData<C> retainById(Collection<String> outputColumns, Collection<String> metricsColumns);

    ReportMetaData<C> retainById(Collection<String> columns);

    /* Retain */

    /* Exclude */

    /* Order */

    ReportMetaData<C> order(ColumnOrder<C>... orderColumns);

    ReportMetaData<C> order(List<ColumnOrder<C>> orderColumns);

    /* Order by id */

    ReportMetaData<C> orderById(List<ColumnOrderTO> columnOrderTOs);

    ReportMetaData<C> orderById(ColumnOrderTO... columnOrderTOs);

    /* Find */

    ReportMetaData<C> metricsOnly();

    void replace(C from, C to);
}
