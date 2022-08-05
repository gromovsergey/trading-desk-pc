package com.foros.session.reporting.siteChannels;

import com.foros.session.reporting.parameters.DatedReportParameters;

public class SiteChannelsReportParameters extends DatedReportParameters {

    private Long accountId;
    private Long siteId;
    private Long tagId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
