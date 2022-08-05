package com.foros.action.reporting.channeltriggers;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.channeltriggers.ChannelTriggerReportData;
import com.foros.session.reporting.channeltriggers.ChannelTriggerReportService;
import com.foros.session.reporting.channeltriggers.ChannelTriggersReportParameters;

import java.io.OutputStream;
import javax.ejb.EJB;

public class RunChannelTriggersReportingAction extends RunReportingActionSupport<ChannelTriggersReportParameters> {

    private static final String CHANNEL_URL_PATTERN = "../../channel/view.action?id=%d";

    @EJB
    private ChannelTriggerReportService reportsService;

    @ReadOnly
    public String execute() {
        return safelyExecute(new ReportWork<ChannelTriggerReportData>() {

            @Override
            protected void executeHtml(ChannelTriggerReportData data) {
                reportsService.processHtml(parameters, CHANNEL_URL_PATTERN, data);
            }

            @Override
            protected void executeExcel(OutputStream os) {
                reportsService.processExcel(parameters, os);
            }

            @Override
            public String getName() {
                return "channelTrigger";
            }
        });
    }

    @ReadOnly
    public String executeSort() {
        return safelyExecute(new ReportWork<ChannelTriggerReportData>() {
            @Override
            protected void executeHtml(ChannelTriggerReportData data) {
                reportsService.processHtml(parameters, CHANNEL_URL_PATTERN, data);
            }
        });
    }
}
