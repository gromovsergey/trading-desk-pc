package com.foros.reporting.tools.query;

import com.foros.reporting.tools.query.parameters.UserTypeParameter;
import com.foros.reporting.tools.query.parameters.UserTypeParameterValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.usertype.UserType;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

abstract class PreparedStatementCreatorSupport implements SqlProvider, ParameterDisposer, PreparedStatementSetter {
    private final List<? extends SqlParameterValue> parameters;
    private List<? extends SqlParameter> declaredParameters;
    protected final String sql;

    public PreparedStatementCreatorSupport(String sql, List<? extends SqlParameter> declaredParameters, List<? extends SqlParameterValue> parameters) {
        checkParameters(declaredParameters, parameters);

        this.sql = sql;
        this.parameters = parameters;
        this.declaredParameters = declaredParameters;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        int firstParameterIndex = getFirstParameterIndex();

        for (int i = 0; i < declaredParameters.size(); i++) {
            int index = i + firstParameterIndex;
            SqlParameter declaredParameter = declaredParameters.get(i);
            SqlParameterValue paramToUse = getParamToUse(declaredParameter);
            UserType type = getUserType(declaredParameter, paramToUse);
            if (type != null) {
                type.nullSafeSet(ps, paramToUse.getValue(), index);
            } else {
                int sqlType = getSqlType(declaredParameter, paramToUse);
                StatementCreatorUtils.setParameterValue(ps, index, sqlType, paramToUse.getValue());
            }
        }
    }

    private int getSqlType(SqlParameter declaredParameter, SqlParameterValue paramToUse) {
        if (paramToUse.getSqlType() != SqlTypeValue.TYPE_UNKNOWN) {
            return paramToUse.getSqlType();
        } else {
            return declaredParameter.getSqlType();
        }
    }

    private UserType getUserType(SqlParameter declaredParameter, SqlParameterValue paramToUse) {
        UserType type = null;
        if (paramToUse instanceof UserTypeParameterValue) {
            type = ((UserTypeParameterValue) paramToUse).getType();
        }

        if (type != null) {
            return type;
        }

        if (declaredParameter instanceof UserTypeParameter) {
            type = ((UserTypeParameter) declaredParameter).getType();
        }
        return type;
    }

    private SqlParameterValue getParamToUse(SqlParameter declaredParameter) {
        for (SqlParameterValue parameter : parameters) {
            if (parameter.getName().equals(declaredParameter.getName())) {
                return parameter;
            }
        }
        throw new RuntimeException("No value supplied for parameter: " + declaredParameter.getName());
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void cleanupParameters() {
        StatementCreatorUtils.cleanupParameters(this.parameters);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(sql).append("\nParameters:\n");
        for (SqlParameterValue value : parameters) {
            Object paramValue = value.getValue();
            buf.append("    ").append(value.getName()).append('=').append(paramValue == null ? "null" : paramValue.toString()).append('\n');
        }
        return buf.toString();
    }

    protected abstract int getFirstParameterIndex();

    private void checkParameters(List<? extends SqlParameter> declaredParameters, List<? extends SqlParameterValue> parameters) {
        for (SqlParameterValue parameter : parameters) {
            boolean found = false;
            for (SqlParameter declaredParameter : declaredParameters) {
                if (declaredParameter.getName().equals(parameter.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new InvalidDataAccessApiUsageException("Parameter " + parameter.getName() + " is not declared");
            }
        }
    }
}
