package com.foros.action.campaign.creative;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.campaign.CampaignCreative;

public class CreativeLinkBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CreativeLinkBreadcrumbsElement(CampaignCreative campaignCreative) {
        super("campaign.breadcrumbs.linked.creative", campaignCreative.getId(), campaignCreative.getCreative().getName(), "campaign/group/creative/view");
    }
}
