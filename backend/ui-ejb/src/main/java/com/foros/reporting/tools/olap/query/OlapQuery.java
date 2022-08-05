package com.foros.reporting.tools.olap.query;

import com.phorm.oix.olap.OlapIdentifier;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.reporting.tools.query.ExecutableQuery;
import com.foros.session.reporting.parameters.DateRange;

import java.util.Collection;
import java.util.List;

public interface OlapQuery extends ExecutableQuery {

    OlapQuery limit(int limit);

    OlapQuery columns(Collection<OlapColumn> columns);

    OlapQuery rows(OlapIdentifier level, DateRange dateRange);

    OlapQuery filter(OlapIdentifier level, DateRange dateRange);

    OlapQuery filter(OlapIdentifier level, String value);

    OlapQuery row(OlapIdentifier level, String value);

    OlapQuery filter(OlapIdentifier level, Long value);

    OlapQuery row(OlapIdentifier level, Long value);

    OlapQuery filter(OlapIdentifier level, List<Long> values);

    OlapQuery filter(OlapIdentifier level, List<Long> values, boolean failIfNotFound);

    OlapQuery row(OlapIdentifier level, List<Long> values, boolean failIfNotFound);

    OlapQuery order(List<ColumnOrder<OlapColumn>> columns);

    OlapQuery order(ColumnOrder<OlapColumn> columnOrder);
}
