package com.foros.reporting.tools.query.impala;

import com.foros.reporting.tools.query.PreparedStatementResultSetExecutor;
import com.foros.reporting.tools.query.ResultSetExecutor;
import com.foros.reporting.tools.template.SqlTemplateSupport;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImpalaStoredFunctionTemplate extends SqlTemplateSupport {
    private String templateSql;

    private Map<String, String> replacements = null;

    public ImpalaStoredFunctionTemplate(String templateSql) {
        super(new PreparedStatementResultSetExecutor());

        this.templateSql = templateSql;
    }

    public ImpalaStoredFunctionTemplate(String templateSql, Map<String, String> replacements) {
        this(templateSql);
        this.replacements = replacements;
    }

    private List<String> checkConsistency(ParametersCollector parametersCollector) {
        List<String> parsedParameters = parametersCollector.parameters;
        for (SqlParameter parameter : executor.getDeclaredParameters()) {
            if (!parsedParameters.contains(parameter.getName())) {
                throw new RuntimeException("wrong parameter : " + parameter.getName());
            }
        }
        return parsedParameters;
    }
    
    @Override
    public ResultSetExecutor build() {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(templateSql);
        ParametersCollector parametersCollector = new ParametersCollector();
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, parametersCollector);

        if (replacements != null) {
            for (String key: replacements.keySet()) {
                sqlToUse = sqlToUse.replaceAll(key, replacements.get(key));
            }
        }

        List<String> parsedParameters = checkConsistency(parametersCollector);

        executor.setSql(sqlToUse);

        List<SqlParameter> all = new ArrayList<>(parsedParameters.size());

        for (String parameterName : parsedParameters) {
            SqlParameter parameter = executor.findParameter(parameterName);
            if (parameter == null) {
                parameter = new SqlParameter(parameterName, SqlTypeValue.TYPE_UNKNOWN);
            }
            all.add(parameter);
        }

        executor.replaceParameters(all);

        return executor;
    }

    @Override
    public String getName() {
        return null;
    }

    private static class ParametersCollector implements SqlParameterSource {
        private List<String> parameters = new ArrayList<>();

        @Override
        public boolean hasValue(String paramName) {
            parameters.add(paramName);
            return false;
        }

        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getSqlType(String paramName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getTypeName(String paramName) {
            throw new UnsupportedOperationException();
        }
    }
}
