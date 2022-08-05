package com.foros.session.campaign.ccg.expressionPerformance;

import com.foros.session.reporting.parameters.ColumnOrderTO;
import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.session.reporting.parameters.Order;
import com.foros.validation.constraint.RequiredConstraint;

public class ExpressionPerformanceReportParameters extends DatedReportParameters {
    @RequiredConstraint
    private Long ccgId;

    private ColumnOrderTO sortColumn = new ColumnOrderTO(ExpressionPerformanceReportMetaData.IMPRESSIONS.getNameKey(), Order.DESC);

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public ColumnOrderTO getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(ColumnOrderTO sortColumn) {
        this.sortColumn = sortColumn;
    }
}
