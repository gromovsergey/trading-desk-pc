package com.foros.session.reporting.pubAdvertising;

import com.foros.session.reporting.OutputType;
import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

public class PubAdvertisingReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private Long accountId;

    private OutputType outputType;
    private String countryCode;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}

