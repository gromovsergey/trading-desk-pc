package com.foros.session.reporting;

import com.foros.reporting.meta.AbstractDependentColumn;
import java.util.List;

public interface AuditableReport<C extends AbstractDependentColumn<C>> {

    void prepare();

    void execute();

    List<PreparedParameter> getPreparedParameters();

    ReportType getReportType();

    Long getRowsCount();

    Long getSize();

    OutputType getOutputType();

    List<C> getColumns();
}
