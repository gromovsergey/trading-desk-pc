package com.foros.reporting.tools.template;

import com.foros.reporting.tools.query.ResultSetExecutor;
import com.foros.reporting.tools.query.ResultSetExecutorSupport;
import com.foros.reporting.tools.query.parameters.TypeToUserTypeAdapter;
import com.foros.reporting.tools.query.parameters.UserTypeParameter;

import java.util.List;

import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.springframework.jdbc.core.SqlParameter;

public abstract class SqlTemplateSupport {
    protected ResultSetExecutorSupport executor;

    protected SqlTemplateSupport(ResultSetExecutorSupport executor) {
        this.executor = executor;
    }

    public SqlTemplateSupport parameter(String name, int sqlType) {
        executor.addParameter(new SqlParameter(name, sqlType));
        return this;
    }

    public SqlTemplateSupport parameter(String name, UserType userType) {
        executor.addParameter(new UserTypeParameter(name, userType));
        return this;
    }

    public SqlTemplateSupport parameter(String name, Type type) {
        executor.addParameter(new UserTypeParameter(name, new TypeToUserTypeAdapter(type)));
        return this;
    }

    protected void appendParameters(StringBuilder sql) {
        List<SqlParameter> declaredParameters = executor.getDeclaredParameters();
        for (int i = 0; i < declaredParameters.size(); i++) {
            if (i != 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
    }

    public abstract ResultSetExecutor build();

    public abstract String getName();
}
