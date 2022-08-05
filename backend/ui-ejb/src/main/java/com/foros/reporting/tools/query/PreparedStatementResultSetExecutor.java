package com.foros.reporting.tools.query;

import com.foros.reporting.ReportingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.support.SQLExceptionTranslator;

public class PreparedStatementResultSetExecutor extends ResultSetExecutorSupport  {

    public PreparedStatementResultSetExecutor() {
    }

    @Override
    public void execute(JdbcTemplate template, final List<? extends SqlParameterValue> params, final Adjuster adjuster, final ResultSetExtractor extractor) {
        PreparedStatementCreator statementCreator = new PreparedStatementCreator() {
            QueryPreparedStatementCreator next = new QueryPreparedStatementCreator(sql, declaredParameters, params);
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return adjuster.adjustPreparedStatement(next.createPreparedStatement(con));
            }
        };

        final SQLExceptionTranslator sqlExceptionTranslator = template.getExceptionTranslator();
        template.execute(statementCreator, new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                try {
                    ResultSet rs = ps.executeQuery();
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
