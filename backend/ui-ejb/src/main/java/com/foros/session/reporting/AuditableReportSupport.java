package com.foros.session.reporting;

import com.foros.reporting.meta.AbstractDependentColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import java.util.List;

public abstract class AuditableReportSupport<C extends AbstractDependentColumn<C>> implements AuditableReport {
    protected AuditResultHandlerWrapper handler;
    protected PreparedParameterBuilder.Factory preparedParameterBuilderFactory;

    public AuditableReportSupport(AuditResultHandlerWrapper handler) {
        this.handler = handler;
    }

    @Override
    public Long getRowsCount() {
        return handler.getRowsCount();
    }

    @Override
    public List<PreparedParameter> getPreparedParameters() {
        return preparedParameterBuilderFactory.auditOnlyBuilder().parameters();
    }

    @Override
    public Long getSize() {
        return handler.getSize();
    }

    @Override
    public OutputType getOutputType() {
        return handler.getOutputType();
    }
}
