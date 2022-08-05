package com.foros.action.reporting.conversionPixels;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.conversionPixels.ConversionPixelsReportParameters;
import com.foros.session.reporting.conversionPixels.ConversionPixelsReportService;

import javax.ejb.EJB;

public class RunConversionPixelsReportingAction extends RunReportingActionSupport<ConversionPixelsReportParameters> {
    @EJB
    private ConversionPixelsReportService reportsService;

    @Override
    @ReadOnly
    public String execute() {
        return safelyExecuteGeneric(reportsService, ReportType.CONVERSION_PIXELS.getName());
    }
}
