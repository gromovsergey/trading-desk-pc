package com.foros.session.reporting.webwise;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.NotEmptyConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SizeConstraint;

import java.util.Collection;
import java.util.LinkedHashSet;

public class WebwiseReportParameters extends DatedReportParameters {

    @SizeConstraint(max = 1000, message = "report.actions.tooManyColocationSelected")
    @NotEmptyConstraint(message = "report.actions.noColocationSelected")
    @RequiredConstraint(message = "report.actions.noColocationSelected")
    private Collection<Long> colocationIds = new LinkedHashSet<Long>();

    private Collection<Long> accountIds = new LinkedHashSet<Long>();

    private Collection<String> countryCodes = new LinkedHashSet<String>();

    public Collection<Long> getColocationIds() {
        return colocationIds;
    }

    public void setColocationIds(Collection<Long> colocationIds) {
        this.colocationIds = colocationIds;
    }

    public Collection<Long> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(Collection<Long> accountIds) {
        this.accountIds = accountIds;
    }

    public Collection<String> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(Collection<String> countryCodes) {
        this.countryCodes = countryCodes;
    }
}
