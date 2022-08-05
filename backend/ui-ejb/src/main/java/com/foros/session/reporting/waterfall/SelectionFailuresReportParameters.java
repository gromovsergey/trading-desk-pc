package com.foros.session.reporting.waterfall;

import org.joda.time.LocalDate;

import com.foros.validation.constraint.RequiredConstraint;

public class SelectionFailuresReportParameters {

    @RequiredConstraint
    private Long ccgId;

    @RequiredConstraint
    private LocalDate date;

    
    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
