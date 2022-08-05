package com.foros.action.reporting.waterfall;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.waterfall.WaterfallReportParameters;
import com.foros.session.reporting.waterfall.WaterfallReportService;

import javax.ejb.EJB;

public class RunWaterfallReportAction extends RunReportingActionSupport<WaterfallReportParameters> {

    @EJB
    private WaterfallReportService reportService;

    @Override
    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportService, "waterfall");
    }

}
