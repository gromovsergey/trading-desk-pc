package com.foros.session.reporting.parameters;

import com.foros.validation.annotation.CascadeValidation;
import com.foros.validation.constraint.RequiredConstraint;

public class DatedReportParameters {
    public static final Long NONE_ID = -1L;

    @CascadeValidation
    @RequiredConstraint
    private DateRange dateRange;

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

}
