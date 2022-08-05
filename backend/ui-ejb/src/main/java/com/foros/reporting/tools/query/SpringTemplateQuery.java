package com.foros.reporting.tools.query;

import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.template.SqlTemplateSupport;

import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlTypeValue;

public class SpringTemplateQuery extends QuerySupport {

    private SqlTemplateSupport sqlTemplate;

    public SpringTemplateQuery(QueryProvider queryProvider, String name, SqlTemplateSupport sqlTemplate, ResultSetValueReaderRegistry defaultReaderRegistry) {
        super(queryProvider, name, defaultReaderRegistry);
        this.sqlTemplate = sqlTemplate;
    }

    @Override
    public Query parameter(String name, Object value) {
        sqlTemplate.parameter(name, SqlTypeValue.TYPE_UNKNOWN);
        return super.parameter(name, value);
    }

    @Override
    public Query parameter(String name, Object value, UserType type) {
        sqlTemplate.parameter(name, type);
        return super.parameter(name, value, type);
    }

    @Override
    public Query parameter(String name, Object value, Type type) {
        sqlTemplate.parameter(name, type);
        return super.parameter(name, value, type);
    }

    @Override
    public Query parameter(String name, Object value, int sqlType) {
        sqlTemplate.parameter(name, sqlType);
        return super.parameter(name, value, sqlType);
    }

    @Override
    public void execute(ResultSetExtractor work) {
        queryProvider.execute(sqlTemplate.build(), parameters, work);
    }
}
