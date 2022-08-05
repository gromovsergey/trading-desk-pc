package com.foros.reporting.tools.query;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

class QueryCallableStatementCreator extends PreparedStatementCreatorSupport implements CallableStatementCreator {

    private int cursorType;

    public QueryCallableStatementCreator(int cursorType, String sql, List<? extends SqlParameter> declaredParameters, List<? extends SqlParameterValue> parameters) {
        super(sql, declaredParameters, parameters);
        this.cursorType = cursorType;
    }

    @Override
    public CallableStatement createCallableStatement(Connection con) throws SQLException {
        CallableStatement cs = con.prepareCall(sql);
        cs.registerOutParameter(1, cursorType);
        setValues(cs);
        return cs;
    }

    @Override
    protected int getFirstParameterIndex() {
        return 2;
    }
}
