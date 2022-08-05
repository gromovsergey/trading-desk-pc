package com.foros.session.query;

import com.foros.session.query.criteria.PaginationCriteria;

public class BusinessQueryImpl implements BusinessQuery {

    private PaginationCriteria criteria;

    public BusinessQueryImpl(PaginationCriteria criteria) {
        this.criteria = criteria;
    }

    public PaginationCriteria getCriteria() {
        return criteria;
    }

    @Override
    public QueryExecutor executor(QueryExecutorService executorService) {
        return executorService.executor(this);
    }

    public BusinessQuery preExecute() {
        return this; 
    }

}
