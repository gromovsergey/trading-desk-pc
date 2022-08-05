package com.foros.session.reporting.channelUsage;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

public class ChannelUsageReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private Long accountId;

    private Long channelId;
    @RequiredConstraint
    private DetailLevel detailLevel = DetailLevel.date;

    public Long getAccountId() {
        return accountId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public DetailLevel getDetailLevel() {
        return detailLevel;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setDetailLevel(DetailLevel detailLevel) {
        this.detailLevel = detailLevel;
    }
}
