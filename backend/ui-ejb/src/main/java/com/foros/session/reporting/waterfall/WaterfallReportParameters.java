package com.foros.session.reporting.waterfall;

import com.foros.validation.constraint.RequiredConstraint;

public class WaterfallReportParameters {

    @RequiredConstraint
    private Long ccgId;

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }
}
