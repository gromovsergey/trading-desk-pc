package com.foros.action.campaign;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.campaign.Campaign;

public class CampaignBreadcrumbsElement extends EntityBreadcrumbsElement {

    public CampaignBreadcrumbsElement(Campaign campaign) {
        super("campaign.breadcrumbs.campaign", campaign.getId(), campaign.getName(), "campaign/view");
    }
}
