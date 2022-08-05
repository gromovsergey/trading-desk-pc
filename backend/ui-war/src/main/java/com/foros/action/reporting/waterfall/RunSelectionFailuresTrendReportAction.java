package com.foros.action.reporting.waterfall;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.waterfall.SelectionFailuresTrendReportParameters;
import com.foros.session.reporting.waterfall.SelectionFailuresTrendReportService;

import javax.ejb.EJB;

public class RunSelectionFailuresTrendReportAction extends RunReportingActionSupport<SelectionFailuresTrendReportParameters> {

    @EJB
    private SelectionFailuresTrendReportService reportService;

    @Override
    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportService, "selectionFailuresTrend");
    }

}
