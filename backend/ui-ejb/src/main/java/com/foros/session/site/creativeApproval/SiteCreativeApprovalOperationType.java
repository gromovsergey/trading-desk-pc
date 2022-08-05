package com.foros.session.site.creativeApproval;

import com.foros.model.site.SiteCreativeApprovalStatus;

public enum SiteCreativeApprovalOperationType {
    APPROVE(SiteCreativeApprovalStatus.APPROVED),
    REJECT(SiteCreativeApprovalStatus.REJECTED),
    RESET(null);
    private final SiteCreativeApprovalStatus target;

    SiteCreativeApprovalOperationType(SiteCreativeApprovalStatus target) {
        this.target = target;
    }

    public SiteCreativeApprovalStatus getTarget() {
        return target;
    }
}
