package com.foros.reporting.tools.query;

import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;

import org.springframework.jdbc.core.ResultSetExtractor;

public class SpringQueryImpl extends QuerySupport {

    protected ResultSetExecutor executor;

    public SpringQueryImpl(QueryProvider queryProvider, ResultSetExecutor executor, ResultSetValueReaderRegistry defaultReaderRegistry) {
        super(queryProvider, executor.getSql(), defaultReaderRegistry);
        this.executor = executor;
    }

    @Override
    public void execute(ResultSetExtractor work) {
        queryProvider.execute(executor, parameters, work);
    }
}
