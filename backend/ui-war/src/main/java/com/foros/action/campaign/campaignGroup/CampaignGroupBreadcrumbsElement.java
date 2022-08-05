package com.foros.action.campaign.campaignGroup;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;

public class CampaignGroupBreadcrumbsElement extends EntityBreadcrumbsElement {
    public CampaignGroupBreadcrumbsElement(CampaignCreativeGroup group) {
        super(getEntityName(group), group.getId(), group.getName(), "campaign/group/view");
    }

    private static String getEntityName(CampaignCreativeGroup group) {
        return group.getCcgType() == CCGType.DISPLAY ? "campaign.breadcrumbs.campaign.group" : "campaign.breadcrumbs.campaign.textGroup";
    }
}
