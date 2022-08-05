package com.foros.session.query;

import com.foros.util.command.executor.HibernateWorkExecutorService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "QueryExecutorService")
public class QueryExecutorServiceBean implements QueryExecutorService {

    @EJB
    private HibernateWorkExecutorService workExecutorService;

    @Override
    public QueryExecutor executor(BusinessQuery query) {
        return new QueryExecutorImpl(workExecutorService, query);
    }

}
