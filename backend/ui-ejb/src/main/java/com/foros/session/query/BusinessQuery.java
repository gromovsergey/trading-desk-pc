package com.foros.session.query;

import com.foros.session.query.criteria.PaginationCriteria;

public interface BusinessQuery {

    PaginationCriteria getCriteria();

    QueryExecutor executor(QueryExecutorService executorService);

    BusinessQuery preExecute();

}
