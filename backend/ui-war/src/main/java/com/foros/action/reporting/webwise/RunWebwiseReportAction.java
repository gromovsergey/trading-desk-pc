package com.foros.action.reporting.webwise;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.webwise.WebwiseReportParameters;
import com.foros.session.reporting.webwise.WebwiseReportService;

import javax.ejb.EJB;
import java.io.OutputStream;

public class RunWebwiseReportAction extends RunReportingActionSupport<WebwiseReportParameters> {

    @EJB
    private WebwiseReportService reportService;

    @ReadOnly
    public String execute() {
        return safelyExecute(new ReportWork<SimpleReportData>() {
            @Override
            protected void executeHtml(SimpleReportData data) {
                reportService.processHtml(parameters, data);
            }

            @Override
            protected void executeCsv(OutputStream os) {
                reportService.processCsv(parameters, os);
            }

            @Override
            protected void executeExcel(OutputStream os) {
                reportService.processExcel(parameters, os);
            }

            @Override
            public String getName() {
                return "webwise";
            }
        });
    }
}
