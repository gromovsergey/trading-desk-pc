package com.foros.reporting.tools.query;

import com.foros.reporting.ReportingException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.support.SQLExceptionTranslator;

public class CallableStatementResultSetExecutor extends ResultSetExecutorSupport {
    private static final Logger logger = Logger.getLogger(CallableStatementResultSetExecutor.class.getName());
    private int cursorType;

    public CallableStatementResultSetExecutor(int cursorType) {
        this.cursorType = cursorType;
    }

    @Override
    public void execute(JdbcTemplate template, final List<? extends SqlParameterValue> parameters, final Adjuster adjuster, final ResultSetExtractor extractor) {

        CallableStatementCreator statementCreator = new CallableStatementCreator() {
            QueryCallableStatementCreator next = new QueryCallableStatementCreator(cursorType, sql, declaredParameters, parameters);
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                return adjuster.adjustCallableStatement(next.createCallableStatement(con));
            }
        };

        final SQLExceptionTranslator sqlExceptionTranslator = template.getExceptionTranslator();
        template.execute(statementCreator, new CallableStatementCallback() {
            @Override
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                cs.execute();
                try {
                    ResultSet rs = (ResultSet) cs.getObject(1);
                    if (rs == null) {
                        logger.log(Level.SEVERE, cs.getWarnings().getMessage() + ".\nQuery:\n" + cs.toString());
                        throw new IllegalStateException();
                    }
                    rs = adjuster.adjustResultSet(rs);
                    extractor.extractData(rs);
                } catch (ReportingException repex) {
                    if (repex.getCause() instanceof SQLException) {
                        throw sqlExceptionTranslator.translate("Fetching from cursor", sql, (SQLException) repex.getCause());
                    } else {
                        throw repex;
                    }
                }
                return null;
            }
        });
    }
}
