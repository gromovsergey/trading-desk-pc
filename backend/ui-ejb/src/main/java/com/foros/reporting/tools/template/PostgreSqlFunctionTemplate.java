package com.foros.reporting.tools.template;

import com.foros.reporting.tools.query.PreparedStatementResultSetExecutor;
import com.foros.reporting.tools.query.ResultSetExecutorSupport;

import java.util.Iterator;
import java.util.List;

public class PostgreSqlFunctionTemplate extends SqlTemplateSupport {

    protected List<String> columns;
    protected String function;

    public PostgreSqlFunctionTemplate(String function) {
        super(new PreparedStatementResultSetExecutor());
        this.function = function;
    }

    @Override
    public String getName() {
        return "SqlFunctionTemplateSupport:" + function;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + function;
    }

    public ResultSetExecutorSupport build() {
        StringBuilder builder = new StringBuilder("select ");

        if (columns != null && !columns.isEmpty()) {
            Iterator<String> iterator = columns.iterator();
            while (iterator.hasNext()) {
                String column = iterator.next();
                builder.append(" \n").append(column);
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
        } else {
            builder.append(" * ");
        }

        builder.append(" from ").append(function).append("(");
        appendParameters(builder);
        builder.append(")");

        executor.setSql(builder.toString());
        return executor;
    }

}
