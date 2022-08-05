package com.foros.action.reporting.custom;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.session.reporting.custom.CustomReportParameters;
import com.foros.session.reporting.custom.olap.CustomPredefinedOlapReportService;

import java.io.OutputStream;
import java.util.Map;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        customValidators =
                @CustomValidator(type = "custconversion", fieldName = "campaignCreativeId", key = "errors.integer",
                        parameters = {@ValidationParameter(name = "fieldKey", value = "report.input.field.CCID")})
)
public class RunOlapCustomReportingAction extends RunReportingActionSupport<CustomReportParameters> {
    @EJB
    private CustomPredefinedOlapReportService reportService;
    private boolean detailsOnly = false;

    @Override
    @ReadOnly
    @InputConfig(methodName = "executeParameters")
    public String execute() {
        parameters.setOutputType(getFormat());
        return safelyExecute(new ReportWork<SimpleReportData>() {
            @Override
            public String getName() {
                return "custom";
            }

            @Override
            protected void executeHtml(SimpleReportData data) {
                reportService.processHtml(parameters, data, true);
            }

            @Override
            protected void executeCsv(OutputStream os) {
                reportService.processCsv(parameters, os);
            }

            @Override
            protected void executeExcel(OutputStream os) {
                reportService.processExcel(parameters, os);
            }
        });
    }

    public String executeParameters() {
        Map conversionErrors = ActionContext.getContext().getConversionErrors();
        if (conversionErrors == null || conversionErrors.isEmpty()) {
            preparedParameters = reportService.prepareParameters(parameters);
        }
        return "input";
    }

    @Override
    protected String success() {
        return detailsOnly ? "detailsOnly" : super.success();
    }

    public boolean isDetailsOnly() {
        return detailsOnly;
    }

    public void setDetailsOnly(boolean detailsOnly) {
        this.detailsOnly = detailsOnly;
    }
}
