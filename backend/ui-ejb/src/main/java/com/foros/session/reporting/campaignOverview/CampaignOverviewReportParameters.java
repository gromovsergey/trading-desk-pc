package com.foros.session.reporting.campaignOverview;

import com.foros.session.reporting.parameters.DatedReportParameters;
import com.foros.validation.constraint.RequiredConstraint;

public class CampaignOverviewReportParameters extends DatedReportParameters {

    @RequiredConstraint
    private Long accountId;

    private boolean segmentByVertical;

    private boolean segmentByProduct;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isSegmentByVertical() {
        return segmentByVertical;
    }

    public void setSegmentByVertical(boolean segmentByVertical) {
        this.segmentByVertical = segmentByVertical;
    }

    public boolean isSegmentByProduct() {
        return segmentByProduct;
    }

    public void setSegmentByProduct(boolean segmentByProduct) {
        this.segmentByProduct = segmentByProduct;
    }
}
