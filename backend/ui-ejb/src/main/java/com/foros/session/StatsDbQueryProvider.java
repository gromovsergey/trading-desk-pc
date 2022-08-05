package com.foros.session;

import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.query.PostgreTimeoutSQLExceptionTranslator;
import com.foros.reporting.tools.query.ResultSetExecutor;
import com.foros.reporting.tools.query.Adjuster;
import com.foros.reporting.tools.template.PostgreCallableProcedureTemplate;
import com.foros.reporting.tools.template.PostgreSqlFunctionTemplate;
import com.foros.reporting.tools.template.SqlTemplateSupport;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;

@LocalBean
@Stateless(name = "StatsDbQueryProvider")
public class StatsDbQueryProvider extends QueryProviderSupport {

    private static final Adjuster FETCH_SIZE_SETTER = new Adjuster() {
        @Override
        public CallableStatement adjustCallableStatement(CallableStatement cs) throws SQLException {
            return adjust(cs);
        }

        @Override
        public PreparedStatement adjustPreparedStatement(PreparedStatement ps) throws SQLException {
            return adjust(ps);
        }

        private <T extends PreparedStatement> T adjust(T statement) throws SQLException {
            statement.setFetchSize(10000);
            return statement;
        }
    };

    @EJB
    private LoggingJdbcTemplate loggingJdbcTemplate;

    @PostConstruct
    public void init() {
        jdbcTemplate = loggingJdbcTemplate;
    }

    @Override
    public SqlTemplateSupport createFunctionTemplate(String function) {
        return new PostgreSqlFunctionTemplate(function);
    }

    @Override
    public SqlTemplateSupport createCallableTemplate(String procedure) {
        return new PostgreCallableProcedureTemplate(procedure, Types.OTHER);
    }

    @Override
    public void doExecute(ResultSetExecutor executor, List<SqlParameterValue> parameters, ResultSetExtractor work) {
        jdbcTemplate.setExceptionTranslator(new PostgreTimeoutSQLExceptionTranslator());
        Adjuster adjuster = Adjuster.sequence(cancelQueryService.statementAdjuster(), FETCH_SIZE_SETTER);
        executor.execute(jdbcTemplate, parameters, adjuster, work);
    }

    @Override
    protected ResultSetValueReaderRegistry getResultSetValueReaderRegistry() {
        return ResultSetValueReaderRegistry.getPostgreDefault();
    }
}
