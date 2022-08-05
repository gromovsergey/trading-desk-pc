package com.foros.action.site;

import com.foros.framework.ReadOnly;
import com.foros.model.site.SiteCreativeApproval;
import com.foros.session.bulk.Paging;
import com.foros.session.query.PartialList;
import com.foros.session.site.creativeApproval.*;

import javax.ejb.EJB;
import java.util.Collections;

public class SaveSiteCreativesApprovalAction extends SiteCreativesApprovalActionSupport {

    @EJB
    private SiteCreativeApprovalService approvalService;

    private SiteCreativeApprovalTO approval = new SiteCreativeApprovalTO();

    public String approve() {
        update(SiteCreativeApprovalOperationType.APPROVE);
        return SUCCESS;
    }

    public String reject() {
        update(SiteCreativeApprovalOperationType.REJECT);
        return SUCCESS;
    }

    private void update(SiteCreativeApprovalOperationType type) {
        SiteCreativeApprovalOperation operation = new SiteCreativeApprovalOperation();
        operation.setType(type);
        operation.setCreativeId(approval.getCreative().getId());
        operation.setVersion(approval.getVersion());
        operation.setFeedback(approval.getFeedback());
        operation.setRejectReason(approval.getRejectReason());
        operation.setPreviousStatus(approval.getApprovalStatus());

        SiteCreativeApproval updatedEntity = approvalService.update(getSite().getId(), operation);

        approval.setVersion(updatedEntity.getApprovalDate());
        approval.setApprovalStatus(type.getTarget());
    }

    @ReadOnly
    public String view() {
        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(getSite().getId());
        selector.setCreativeIds(Collections.singleton(approval.getCreative().getId()));
        selector.setPaging(new Paging(0, Paging.MAX_PAGE_SIZE));
        PartialList<SiteCreativeApprovalTO> existingApprovals = approvalService.searchCreativeApprovals(selector);

        if (!existingApprovals.isEmpty()) {
            approval = existingApprovals.get(0);
        } else {
            approval = null;
        }

        return SUCCESS;
    }

    public SiteCreativeApprovalTO getApproval() {
        return approval;
    }

    public void setApproval(SiteCreativeApprovalTO approval) {
        this.approval = approval;
    }
}
