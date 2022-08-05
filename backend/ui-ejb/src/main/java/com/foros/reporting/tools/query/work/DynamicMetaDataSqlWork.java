package com.foros.reporting.tools.query.work;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.MetaDataBuilder;
import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DynamicMetaDataSqlWork implements ResultSetExtractor {

    private ResultHandler handler;
    private ResultSetValueReaderRegistry readerRegistry;

    public DynamicMetaDataSqlWork(ResultHandler handler, ResultSetValueReaderRegistry readerRegistry) {
        this.handler = handler;
        this.readerRegistry = readerRegistry;
    }

    @Override
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        new SimpleSqlWork(
                MetaDataBuilder.metaData(rs.getMetaData()),
                handler,
                readerRegistry
        ).extractData(rs);

        return null;
    }

}
