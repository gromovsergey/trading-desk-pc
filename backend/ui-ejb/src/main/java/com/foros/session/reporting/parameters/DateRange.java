package com.foros.session.reporting.parameters;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import com.foros.validation.constraint.RequiredConstraint;

public class DateRange {

    @RequiredConstraint
    private LocalDate begin;

    @RequiredConstraint
    private LocalDate end;

    public DateRange() {
    }

    public DateRange(LocalDate begin, LocalDate end) {
        this.begin = begin;
        this.end = end;
    }

    public List<LocalDate> getRangeDates() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate iter = begin;
        while (iter.compareTo(end) <= 0) {
            dates.add(iter);
            iter = iter.plusDays(1);
        }
        return dates;
    }

    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public LocalDate getBegin() {
        return begin;
    }

    public void setBegin(LocalDate begin) {
        this.begin = begin;
    }

    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public boolean isNullRange() {
        return (begin == null) && (end == null);
    }

    @Override
    public String toString() {
        return begin + " - " + end;
    }
}
