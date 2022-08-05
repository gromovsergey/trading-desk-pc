package com.foros.session.reporting.channel;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.NotEmptyConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.HashSet;
import java.util.Set;

public class ChannelReportParameters extends DatedReportParameters {

    @RequiredConstraint(message = "report.actions.noChannelsSelected")
    private Long channelId;

    @NotEmptyConstraint
    private Set<String> outputCols = new HashSet<String>();

    @NotEmptyConstraint
    private Set<String> metricCols = new HashSet<String>();

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Set<String> getOutputCols() {
        return outputCols;
    }

    public void setOutputCols(Set<String> outputCols) {
        this.outputCols = outputCols;
    }

    public Set<String> getMetricCols() {
        return metricCols;
    }

    public void setMetricCols(Set<String> metricCols) {
        this.metricCols = metricCols;
    }
}
