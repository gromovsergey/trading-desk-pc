package com.foros.reporting.tools.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

class QueryPreparedStatementCreator extends PreparedStatementCreatorSupport implements PreparedStatementCreator, PreparedStatementSetter {

    public QueryPreparedStatementCreator(String sql, List<? extends SqlParameter> declaredParameters, List<? extends SqlParameterValue> parameters) {
        super(sql, declaredParameters, parameters);
    }

    @Override
    protected int getFirstParameterIndex() {
        return 1;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        setValues(ps);
        return ps;
    }
}
