package com.foros.reporting.tools.query;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class SqlQuery {

    private String sql;
    private SqlParameterSource parameters;

    public SqlQuery(String sql, SqlParameterSource parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    public String getSql() {
        return sql;
    }

    public SqlParameterSource getParameters() {
        return parameters;
    }

}
