package com.foros.reporting.tools.query;

import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.query.cache.CachedQuery;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.ResultSetExtractor;

public interface Query extends ExecutableQuery {

    Query parameter(String name, Object value);

    Query parameter(String name, Object value, UserType type);

    Query parameter(String name, Object value, Type type);

    Query parameter(String name, Object value, int sqlType);

    Query readerRegistry(ResultSetValueReaderRegistry readerRegistry);

    CachedQuery cached();

    void execute(ResultSetExtractor work);
}
