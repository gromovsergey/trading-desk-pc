package com.foros.session.reporting.parameters;

import com.foros.validation.constraint.RequiredConstraint;

import org.joda.time.LocalDateTime;

public class DateTimeRange {

    @RequiredConstraint
    private LocalDateTime begin;

    @RequiredConstraint
    private LocalDateTime end;

    public DateTimeRange() {
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
        this.begin = begin;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
