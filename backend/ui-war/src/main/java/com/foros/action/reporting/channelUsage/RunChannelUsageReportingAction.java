package com.foros.action.reporting.channelUsage;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.channelUsage.ChannelUsageReportParameters;
import com.foros.session.reporting.channelUsage.ChannelUsageReportService;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import javax.ejb.EJB;

@Validations(
        conversionErrorFields = @ConversionErrorFieldValidator(fieldName = "channelId", key = "report.channelUsage.invalidChannel")
)
public class RunChannelUsageReportingAction extends RunReportingActionSupport<ChannelUsageReportParameters> {

    @EJB
    private ChannelUsageReportService channelUsageReportService;

    @ReadOnly
    public String execute() throws Exception {
        return safelyExecuteGeneric(channelUsageReportService, "channelUsage");
    }
}

