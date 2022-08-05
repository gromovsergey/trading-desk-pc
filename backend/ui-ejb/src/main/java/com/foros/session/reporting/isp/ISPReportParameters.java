package com.foros.session.reporting.isp;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

public class ISPReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private ISPReportType reportType;

    @RequiredConstraint
    private Long accountId;

    private Long colocationId;

    public ISPReportType getReportType() {
        return reportType;
    }

    public void setReportType(ISPReportType reportType) {
        this.reportType = reportType;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getColocationId() {
        return colocationId;
    }

    public void setColocationId(Long colocationId) {
        this.colocationId = colocationId;
    }
}
