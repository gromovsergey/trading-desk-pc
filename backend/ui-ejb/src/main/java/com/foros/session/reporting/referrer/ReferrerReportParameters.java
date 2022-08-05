package com.foros.session.reporting.referrer;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "referrerReportParameters")
@XmlType(propOrder = {
        "accountId",
        "siteId",
        "tagId"
})
public class ReferrerReportParameters extends DatedReportParameters {

    @RequiredConstraint
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
