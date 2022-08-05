package com.foros.reporting.tools.query.work;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.jdbc.ResultSetRowSource;
import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.strategy.SimpleIterationStrategy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleSqlWork implements ResultSetExtractor {

    private IterationStrategy iterationStrategy;
    private ResultSetValueReaderRegistry readerRegistry;
    private ResultHandler handler;

    public SimpleSqlWork(MetaData metaData, ResultHandler handler, ResultSetValueReaderRegistry readerRegistry) {
        this(new SimpleIterationStrategy(metaData), readerRegistry, handler);
    }

    public SimpleSqlWork(IterationStrategy iterationStrategy, ResultSetValueReaderRegistry readerRegistry, ResultHandler handler) {
        this.iterationStrategy = iterationStrategy;
        this.readerRegistry = readerRegistry;
        this.handler = handler;
    }

    @Override
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        iterationStrategy.process(new ResultSetRowSource(rs, readerRegistry), handler);
        return null;
    }

}
