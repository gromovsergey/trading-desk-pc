package com.foros.session.reporting.profiling;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

public class ProfilingReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private Long accountId;


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
