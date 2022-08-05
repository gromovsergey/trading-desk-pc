package com.foros.reporting.tools.query;

import com.foros.reporting.ResultHandler;
import com.foros.reporting.meta.MetaData;
import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.query.cache.CachedQuery;
import com.foros.reporting.tools.query.parameters.SqlTypeParameterValue;
import com.foros.reporting.tools.query.parameters.TypeToUserTypeAdapter;
import com.foros.reporting.tools.query.parameters.UserTypeParameterValue;
import com.foros.reporting.tools.query.strategy.IterationStrategy;
import com.foros.reporting.tools.query.work.SimpleSqlWork;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;

public abstract class QuerySupport implements Query {

    private String name;

    protected List<SqlParameterValue> parameters;
    protected QueryProvider queryProvider;
    protected ResultSetValueReaderRegistry readerRegistry;

    public QuerySupport(QueryProvider queryProvider, String name, ResultSetValueReaderRegistry defaultReaderRegistry) {
        this.name = name;
        this.parameters = new ArrayList<SqlParameterValue>();
        this.readerRegistry = defaultReaderRegistry;
        this.queryProvider = queryProvider;
    }

    @Override
    public CachedQuery cached() {
        return queryProvider.cached(this, name);
    }

    @Override
    public Query parameter(String name, Object value) {
        return parameter(new SqlTypeParameterValue(name, SqlTypeValue.TYPE_UNKNOWN, value));
    }

    @Override
    public Query parameter(String name, Object value, UserType type) {
        return parameter(new UserTypeParameterValue(name, type, value));
    }

    @Override
    public Query parameter(String name, Object value, Type type) {
        return parameter(new UserTypeParameterValue(name, new TypeToUserTypeAdapter(type), value));
    }

    @Override
    public Query parameter(String name, Object value, int sqlType) {
        return parameter(new SqlTypeParameterValue(name, sqlType, value));
    }

    protected int indexOf(String name) {
        for (int i = 0; i < parameters.size(); i++) {
            SqlParameterValue parameter = parameters.get(i);
            if (parameter.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private Query parameter(SqlParameterValue parameterValue) {
        int index = indexOf(parameterValue.getName());
        if (index == -1) {
            parameters.add(parameterValue);
        } else {
            parameters.set(index, parameterValue);
        }
        return this;
    }

    @Override
    public Query readerRegistry(ResultSetValueReaderRegistry readerRegistry) {
        this.readerRegistry = readerRegistry;
        return this;
    }

    public abstract void execute(ResultSetExtractor work);

    @Override
    public void execute(ResultHandler handler, IterationStrategy iterationStrategy) {
        execute(new SimpleSqlWork(iterationStrategy, readerRegistry, handler));
    }

    @Override
    public void execute(ResultHandler handler, MetaData metaData) {
        execute(new SimpleSqlWork(metaData, handler, readerRegistry));
    }
}
