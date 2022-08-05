package com.foros.reporting.tools.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.SqlParameter;

public abstract class ResultSetExecutorSupport implements ResultSetExecutor {
    protected final List<SqlParameter> declaredParameters = new LinkedList<SqlParameter>();
    protected String sql;

    public void addParameter(SqlParameter param) {
        declaredParameters.add(param);
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public List<SqlParameter> getDeclaredParameters() {
        return declaredParameters;
    }

    @Override
    public boolean hasDeclaredParameter(String name) {
        for (SqlParameter parameter : declaredParameters) {
            if (parameter.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void replaceParameters(List<SqlParameter> declaredParameters) {
        this.declaredParameters.clear();
        this.declaredParameters.addAll(new ArrayList<SqlParameter>(declaredParameters));
    }

    public SqlParameter findParameter(String name) {
        for (SqlParameter declaredParameter : declaredParameters) {
            if (declaredParameter.getName().equals(name)) {
                return declaredParameter;
            }
        }
        return null;
    }


    @Override
    public String getSql() {
        return sql;
    }
}
