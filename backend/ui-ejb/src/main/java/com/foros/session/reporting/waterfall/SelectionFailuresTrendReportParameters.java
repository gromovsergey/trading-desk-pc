package com.foros.session.reporting.waterfall;

import com.foros.validation.constraint.RequiredConstraint;

public class SelectionFailuresTrendReportParameters {

    @RequiredConstraint
    private Long ccgId;

    @RequiredConstraint
    private Integer mask;

    
    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public Integer getMask() {
        return mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }
}
