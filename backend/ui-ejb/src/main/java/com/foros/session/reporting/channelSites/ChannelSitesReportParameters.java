package com.foros.session.reporting.channelSites;

import com.foros.session.reporting.parameters.DatedReportParameters;

public class ChannelSitesReportParameters extends DatedReportParameters{
    private Long accountId;

    private Long channelId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
