package com.foros.action.campaign.campaignCredit;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.campaign.CampaignCreditAllocation;

public class CampaignCreditAllocationBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CampaignCreditAllocationBreadcrumbsElement(CampaignCreditAllocation allocation) {
        super("CampaignCreditAllocation.entityName", allocation.getCampaignCredit().getId(), String.valueOf(allocation.getId()), "campaignCredit/view");
    }
}
