package com.foros.action.campaign.campaignCredit;

import com.foros.breadcrumbs.EntityBreadcrumbsElement;
import com.foros.model.campaign.CampaignCredit;

public class ManageCampaignCreditBreadcrumbsElement extends EntityBreadcrumbsElement {
    public ManageCampaignCreditBreadcrumbsElement(CampaignCredit credit) {
        super("CampaignCredit.entityName.manage", credit.getId(), String.valueOf(credit.getId()), "campaignCredit/view");
    }
}
