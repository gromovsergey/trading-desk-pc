package com.foros.session.reporting.inventoryEstimation;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.math.BigDecimal;

public class InventoryEstimationReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private Long accountId;
    private Long siteId;
    private Long tagId;

    @RequiredConstraint
    @RangeConstraint(min = "0", max = "100")
    private BigDecimal  reservedPremium;

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

    public BigDecimal getReservedPremium() {
        return reservedPremium;
    }

    public void setReservedPremium(BigDecimal reservedPremium) {
        this.reservedPremium = reservedPremium;
    }

}
