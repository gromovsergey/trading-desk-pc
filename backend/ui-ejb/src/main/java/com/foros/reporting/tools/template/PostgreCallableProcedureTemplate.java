package com.foros.reporting.tools.template;

import com.foros.reporting.tools.query.CallableStatementResultSetExecutor;
import com.foros.reporting.tools.query.ResultSetExecutor;


public class PostgreCallableProcedureTemplate extends SqlTemplateSupport {
    private String procedure;

    public PostgreCallableProcedureTemplate(String procedure, int cursorType) {
        super(new CallableStatementResultSetExecutor(cursorType));
        this.procedure = procedure;
    }

    @Override
    public ResultSetExecutor build() {
        StringBuilder sql = new StringBuilder();
        sql.append("{ ? = call ");
        sql.append(procedure);
        sql.append("(");
        appendParameters(sql);
        sql.append(") }");

        executor.setSql(sql.toString());
        return executor;
    }

    @Override
    public String getName() {
        return "PostgreCallableProcedureTemplate:" + procedure;
    }
}
