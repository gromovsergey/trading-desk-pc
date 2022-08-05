package com.foros.reporting.tools.query.parameters;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

public class SqlTypeParameterValue extends SqlParameterValue {
    public SqlTypeParameterValue(String name, int sqlType, Object value) {
        super(new SqlParameter(name, sqlType), value);
    }
}
