package com.foros.action.reporting.conversions;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.conversions.ConversionsReportParameters;
import com.foros.session.reporting.conversions.ConversionsReportService;

import javax.ejb.EJB;

public class RunConversionsReportingAction extends RunReportingActionSupport<ConversionsReportParameters> {
    @EJB
    private ConversionsReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, "conversions");
    }
}
