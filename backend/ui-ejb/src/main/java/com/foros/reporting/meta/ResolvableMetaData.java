package com.foros.reporting.meta;

import java.util.List;

public interface ResolvableMetaData<C extends DependentColumn<C>> {

    List<C> getColumns();

    List<C> getMetricsColumns();

    List<C> getOutputColumns();

    ReportMetaData<C> resolve(Object context);
}
