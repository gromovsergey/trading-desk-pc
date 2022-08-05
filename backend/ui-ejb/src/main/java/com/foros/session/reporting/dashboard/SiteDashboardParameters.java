package com.foros.session.reporting.dashboard;

import com.foros.validation.constraint.RequiredConstraint;

public class SiteDashboardParameters extends DashboardParameters {

    @RequiredConstraint
    private Long siteId;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

}
