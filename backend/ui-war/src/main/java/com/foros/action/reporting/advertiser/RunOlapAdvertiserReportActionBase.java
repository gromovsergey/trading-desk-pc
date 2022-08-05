package com.foros.action.reporting.advertiser;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.List;

public abstract class RunOlapAdvertiserReportActionBase extends RunReportingActionSupport<OlapAdvertiserReportParameters> {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("accountId", "getText('report.advertising.selectAccount')")
            .rules();

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(getService(), getFileName());
    }

    protected abstract OlapAdvertiserReportService getService();

    protected abstract String getFileName();

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }
}
