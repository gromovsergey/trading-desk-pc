package com.foros.session.reporting.userAgents;

import com.foros.session.reporting.parameters.LocalDateXmlAdapter;
import com.foros.validation.constraint.RequiredConstraint;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.joda.time.LocalDate;

public class UserAgentsReportParameters {

    @RequiredConstraint
    private LocalDate date;

    @XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
