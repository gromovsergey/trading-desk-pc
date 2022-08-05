package com.foros.session;

import com.foros.reporting.rowsource.jdbc.ResultSetValueReaderRegistry;
import com.foros.reporting.tools.query.ResultSetExecutor;
import com.foros.reporting.tools.query.impala.ImpalaStoredFunctionTemplate;
import com.foros.reporting.tools.query.impala.ImpalaUnavailableSQLExceptionTranslator;
import com.foros.reporting.tools.template.SqlTemplateSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import java.util.Map;

@LocalBean
@Stateless(name = "ImpalaDbQueryProvider")
public class ImpalaDbQueryProvider extends QueryProviderSupport {

    @EJB
    private ImpalaJdbcTemplate impalaJdbcTemplate;

    @PostConstruct
    public void init() {
        jdbcTemplate = impalaJdbcTemplate;
    }

    private String getSQL(String function) {
    	return impalaJdbcTemplate.queryForObject("select source from forosfunctions where function_schema = 'report' and name = '" + function + "'",
                new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("source");
            }
        });
    }
    
    @Override
    public SqlTemplateSupport createFunctionTemplate(String function) {
        return new ImpalaStoredFunctionTemplate(getSQL(function));
    }

    public SqlTemplateSupport createFunctionTemplate(String function, Map<String, String> replacements) {
        return new ImpalaStoredFunctionTemplate(getSQL(function), replacements);
    }

    @Override
    public SqlTemplateSupport createCallableTemplate(String procedure) {
        throw new IllegalStateException("Impala doesn't support procedures");
    }

    @Override
    public void doExecute(ResultSetExecutor executor, List<SqlParameterValue> parameters, ResultSetExtractor work) {
        jdbcTemplate.setExceptionTranslator(new ImpalaUnavailableSQLExceptionTranslator());
        executor.execute(jdbcTemplate, parameters, cancelQueryService.statementAdjuster(), work);
    }

    @Override
    protected ResultSetValueReaderRegistry getResultSetValueReaderRegistry() {
        return ResultSetValueReaderRegistry.getImpalaDefault();
    }
}