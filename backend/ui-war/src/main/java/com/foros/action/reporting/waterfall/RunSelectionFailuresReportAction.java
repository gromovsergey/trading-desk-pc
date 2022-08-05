package com.foros.action.reporting.waterfall;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.waterfall.SelectionFailuresReportParameters;
import com.foros.session.reporting.waterfall.SelectionFailuresReportService;

import javax.ejb.EJB;

public class RunSelectionFailuresReportAction extends RunReportingActionSupport<SelectionFailuresReportParameters> {

    @EJB
    private SelectionFailuresReportService reportService;

    @Override
    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportService, "selectionFailures");
    }

}
