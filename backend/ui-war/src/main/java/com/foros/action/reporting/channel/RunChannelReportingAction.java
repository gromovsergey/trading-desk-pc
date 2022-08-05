package com.foros.action.reporting.channel;

import com.foros.action.reporting.RunReportingActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.reporting.channel.ChannelReportParameters;
import com.foros.session.reporting.channel.ChannelReportService;

import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;


public class RunChannelReportingAction extends RunReportingActionSupport<ChannelReportParameters> {
    @EJB
    private ChannelReportService reportsService;

    @Override
    @ReadOnly
    public String execute() {
        prepareParameters();
        return safelyExecuteGeneric(reportsService, "channel");
    }

    private void prepareParameters() {
        parameters.setOutputCols(prepareColumnNames(parameters.getOutputCols()));
        parameters.setMetricCols(prepareColumnNames(parameters.getMetricCols()));
    }

    private Set<String> prepareColumnNames(Set<String> cols) {
        Set<String> resultCols = new HashSet<>(cols.size());
        for (String col : cols) {
            resultCols.add(col.replace(".long", ""));
        }
        return resultCols;
    }
}
